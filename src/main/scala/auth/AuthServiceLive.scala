package auth

import auth.model.{IntrospectResponse, IntrospectResponseFailure, IntrospectResponseSuccess}
import config.AppConfig
import zio.http.Header.Authorization
import zio.http.{Credentials, Request, Response}
import zio.redis.Redis
import zio.{durationLong, Config, IO, Task, ZIO, ZLayer}

import java.time.Instant

case class AuthServiceLive(
    redis: Redis,
    config: AppConfig
) extends AuthService {

  override def validateAuth(request: Request): IO[Response, Boolean] = {

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

  private def validateBearerAuth(jwt: String): IO[Response, Boolean] = {
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
        .tapError(err => ZIO.logWarning(s"Error: ${err.getMessage}"))

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

  private def introspectToken(jwt: String): Task[IntrospectResponse] = {
    // TODO: Добавить запрос к кейклоку
    ZIO.succeed(IntrospectResponseFailure(false))
  }
}

object AuthServiceLive {
  lazy val layer: ZLayer[Redis, Config.Error, AuthServiceLive] = ZLayer {
    for {
      config <- AppConfig.get
      redis  <- ZIO.service[Redis]
      client = AuthServiceLive(redis, config)
    } yield client
  }
}
