package auth.keycloak

import auth.models.{IntrospectResponse, User, UserInfo}
import config.{AppConfig, Keycloak}
import exception.Exceptions._
import util.parse.JsonParseOps.bodyParse
import util.secret.Secret.SecretOps
import zio.http.{Body, Client, Form, Header, Response}
import zio.json.JsonDecoder
import zio.redis.Redis
import zio.{Config, RIO, Scope, Task, ZIO, ZLayer}

import java.net.URI

case class KeycloakAuthorizerLive(
    redis: Redis,
    config: Keycloak,
    httpClient: Client
) extends KeycloakAuthorizer {

  override def userInfo(token: String): RIO[Scope, User] = {
    val pathSuffix = s"/realms/${config.realm}/protocol/openid-connect/userinfo"
    val sendUserInfo = httpClient
      .uri(URI.create(config.host))
      .addHeader(Header.Authorization.Bearer(token))
      .get(pathSuffix)

    process(sendUserInfo)((body: String) => bodyParse[UserInfo](body))
      .map(info => User(info.username))
  }

  override def introspectToken(token: String): RIO[Scope, IntrospectResponse] = {
    val pathSuffix = s"/realms/${config.realm}/protocol/openid-connect/token/introspect"
    val body = Body.fromURLEncodedForm(
      Form.fromStrings(
        ("client_id", config.clientId),
        ("client_secret", config.clientSecret.secretToString),
        ("token", token)
      )
    )
    val sendIntrospect = httpClient
      .uri(URI.create(config.host))
      .post(pathSuffix)(body)

    process(sendIntrospect)((body: String) => bodyParse[IntrospectResponse](body))
  }

  private def process[A: JsonDecoder, R, T <: Response](
      effect: RIO[R, T]
  )(
      decode: String => Task[A]
  ): RIO[R, A] =
    for {
      response <- effect.mapError(err => AuthException(s"Cannot authorize user in KeyCloak: $err"))
      _        <- ZIO.fail(AuthException("Cannot authorize user in KeyCloak")).when(response.status.isError)
      info <- response.body.asString
        .mapError(err => BodyParsingException(s"Cannot decode body: $err"))
        .flatMap(body => decode(body))
    } yield info
}

object KeycloakAuthorizerLive {

  val layer: ZLayer[Client with Redis, Config.Error, KeycloakAuthorizer] = ZLayer {
    for {
      config     <- AppConfig.get(_.keycloak)
      redis      <- ZIO.service[Redis]
      httpClient <- ZIO.service[Client]
      client = KeycloakAuthorizerLive(redis, config, httpClient)
    } yield client
  }
}
