package auth.keycloak

import auth.models.{IntrospectResponse, User, UserInfo}
import config.{AppConfig, Keycloak}
import exception.Exceptions._
import zio.http.{Body, Client, Form, Header}
import zio.json.DecoderOps
import zio.redis.Redis
import zio.{Config, RIO, Scope, ZIO, ZLayer}

import java.net.URI

case class KeycloakAuthorizerLive(
    redis: Redis,
    config: Keycloak,
    httpClient: Client
) extends KeycloakAuthorizer {

  def userInfo(token: String): RIO[Scope, User] = {
    val pathSuffix = s"/realms/${config.realm}/protocol/openid-connect/userinfo"

    for {
      response <- httpClient
        .uri(URI.create(config.host))
        .addHeader(Header.Authorization.Bearer(token))
        .get(pathSuffix)
        .mapError(err => AuthException(s"Cannot authorize user in KeyCloak: $err"))
      _ <- ZIO.fail(AuthException("Cannot authorize user in KeyCloak")).when(response.status.isError)
      userInfo <- response.body.asString
        .mapError(err => AuthException(s"Cannot decode body: $err"))
        .flatMap(body =>
          ZIO
            .fromEither(body.fromJson[UserInfo])
            .mapError(err => AuthException(s"Cannot decode body: $err"))
        )
    } yield User(userInfo.username)
  }

  def introspectToken(token: String): RIO[Scope, IntrospectResponse] = {
    import util._

    val pathSuffix = s"/realms/${config.realm}/protocol/openid-connect/token/introspect"
    val body = Body.fromURLEncodedForm(
      Form.fromStrings(
        ("client_id", config.clientId),
        ("client_secret", config.clientSecret.secretToString),
        ("token", token)
      )
    )

    httpClient
      .uri(URI.create(config.host))
      .post(pathSuffix)(body)
      .flatMap { response =>
        response.body.asString
          .mapBoth(
            err => InternalException(err.getMessage),
            body =>
              ZIO
                .fromEither(body.fromJson[IntrospectResponse])
                .orElseFail(BodyParsingException("Fail to decode json"))
          )
          .flatten
      }
  }
}

object KeycloakAuthorizerLive {
  lazy val layer: ZLayer[Client with Redis, Config.Error, KeycloakAuthorizer] = ZLayer {
    for {
      config     <- AppConfig.get(_.keycloak)
      redis      <- ZIO.service[Redis]
      httpClient <- ZIO.service[Client]
      client = KeycloakAuthorizerLive(redis, config, httpClient)
    } yield client
  }
}
