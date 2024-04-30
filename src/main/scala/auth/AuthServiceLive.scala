package auth

import auth.model.{IntrospectResponse, IntrospectResponseFailure, IntrospectResponseSuccess}
import config.AppConfig
import exception.Exceptions.{BodyParsingException, InternalException}
import zio.http.Header.Authorization
import zio.http.{Body, Client, Credentials, Form, Request, Response, ZClient}
import zio.json.DecoderOps
import zio.redis.Redis
import zio.{durationLong, IO, Scope, ZIO, ZLayer}

import java.net.URI
import java.time.Instant

case class AuthServiceLive(
    redis: Redis,
    config: AppConfig,
    httpClient: Client
) extends AuthService {

  override def validateAuth(request: Request): ZIO[Scope, Response, Boolean] = {

    val token = request.header(Authorization)

    token match {
      case Some(Authorization.Basic(userName, password)) => validateBasicAuth(Credentials(userName, password))
      case Some(Authorization.Bearer(token))             => validateBearerAuth(token.value.mkString)
      case _                                             => ZIO.succeed(false)
    }
  }

  private def validateBasicAuth(credentials: Credentials): IO[Response, Boolean] = {
    import util._

    val secretLogin    = config.basicAuth.login.secretToString
    val secretPassword = config.basicAuth.password.secretToString

    if (secretLogin.contains(credentials.uname) && secretPassword.contains(credentials.upassword.secretToString)) ZIO.succeed(true)
    else ZIO.succeed(false)
  }

  private def validateBearerAuth(jwt: String): ZIO[Scope, Response, Boolean] = {
    def getKey(token: String) = s"auth:keycloak:$token"

    val now: Long = Instant.now.getEpochSecond

    val introspectResponse =
      redis
        .get(getKey(jwt))
        .returning[IntrospectResponseSuccess]
        .flatMap {
          case Some(response) => ZIO.succeed(response)
          case None           => introspectToken(jwt)
        }

    introspectResponse
      .flatMap {
        case IntrospectResponseFailure(_) => ZIO.succeed(false)
        case IntrospectResponseSuccess(exp, name, realmAccess, active) =>
          redis
            .set(getKey(jwt), IntrospectResponseSuccess(exp, name, realmAccess, active), Some((exp - now).seconds))
            .as(true)
      }
      .tapError(err => ZIO.logWarning(s"Error: ${err.getMessage}"))
      .mapError(err => Response.internalServerError(err.getMessage))
  }

  private def introspectToken(token: String): ZIO[Scope, Throwable, IntrospectResponse] = {
    import util._

    val pathSuffix = s"/realms/${config.keycloak.realm}/protocol/openid-connect/token/introspect"
    val body = Body.fromURLEncodedForm(
      Form.fromStrings(
        ("client_id", config.keycloak.clientId),
        ("client_secret", config.keycloak.clientSecret.secretToString),
        ("token", token)
      )
    )

    httpClient
      .uri(URI.create(config.keycloak.host))
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

object AuthServiceLive {
  lazy val layer: ZLayer[Redis, Throwable, AuthService] = ZClient.default >>> ZLayer {
    for {
      config     <- AppConfig.get
      redis      <- ZIO.service[Redis]
      httpClient <- ZIO.service[Client]
      client = AuthServiceLive(redis, config, httpClient)
    } yield client
  }
}
