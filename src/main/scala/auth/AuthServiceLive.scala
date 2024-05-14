package auth

import auth.keycloak.KeycloakAuthorizer
import auth.models._
import config.AppConfig
import exception.Exceptions.AuthException
import io.grpc.Metadata
import scalapb.zio_grpc.RequestContext
import util.Secret.SecretOps
import zio.http.Header.Authorization
import zio.http.Request
import zio.redis.Redis
import zio.{durationLong, Config, RIO, Scope, Task, UIO, ZIO, ZLayer}

import java.time.Instant
import java.util.Base64

case class AuthServiceLive(
    redis: Redis,
    config: AppConfig,
    keycloakAuthorizer: KeycloakAuthorizer
) extends AuthService {

  private val decoder = Base64.getUrlDecoder

  private val authKey = Metadata.Key.of(
    "Authorization",
    Metadata.ASCII_STRING_MARSHALLER
  )

  override def validateHeader(request: Request): RIO[Scope, AuthResult] =
    request.header(Authorization) match {
      case Some(header) => validateToken(header.renderedValue)
      case None         => ZIO.fail(AuthException("Authorization field is empty"))
    }

  override def validateContext(context: RequestContext): RIO[Scope, AuthResult] =
    for {
      token  <- extractMetadata(context)
      result <- validateToken(token)
    } yield result

  private def validateToken(token: String): RIO[Scope, AuthResult] =
    token match {
      case s"Basic $payload" =>
        for {
          payload  <- ZIO.attempt(new String(decoder.decode(payload)))
          authData <- fromPayloadString(payload)
          result   <- validateBasicAuth(authData)
        } yield result
      case s"Bearer $payload" => validateBearerAuth(payload)
      case _                  => ZIO.fail(AuthException(s"Unexpected value"))
    }

  private def validateBasicAuth(credentials: BasicAuthData): UIO[AuthResult] = {
    val secretLogin    = config.basicAuth.login.secretToString
    val secretPassword = config.basicAuth.password.secretToString

    if (secretLogin.contains(credentials.username) && secretPassword.contains(credentials.password))
      ZIO.succeed(ValidAuthResult(credentials.username, List("default")))
    else ZIO.succeed(InvalidAuthResult(credentials.username))
  }

  private def validateBearerAuth(jwt: String): RIO[Scope, AuthResult] = {
    def getKey(token: String) = s"auth:keycloak:$token"

    val now: Long = Instant.now.getEpochSecond

    val introspectResponse =
      redis
        .get(getKey(jwt))
        .returning[IntrospectResponseSuccess]
        .flatMap {
          case Some(response) => ZIO.succeed(response)
          case None           => keycloakAuthorizer.introspectToken(jwt)
        }

    introspectResponse
      .flatMap {
        case IntrospectResponseFailure(_) => ZIO.succeed(InvalidAuthResult())
        case IntrospectResponseSuccess(exp, name, realmAccess, active) =>
          redis
            .set(getKey(jwt), IntrospectResponseSuccess(exp, name, realmAccess, active), Some((exp - now).seconds))
            .as(ValidAuthResult(name, realmAccess.roles))
      }
      .tapError(err => ZIO.logWarning(s"Error: ${err.getMessage}"))
  }

  private def extractMetadata(context: RequestContext): Task[String] =
    context.metadata.get(authKey).flatMap {
      case Some(token) => ZIO.succeed(token)
      case None        => ZIO.fail(AuthException(s"Unexpected value for Authorization field: $authKey"))
    }

  private def fromPayloadString(payload: String): Task[BasicAuthData] = payload match {
    case s"$username:$password" => ZIO.succeed(BasicAuthData(username, password))
    case unexpected             => ZIO.fail(AuthException(s"Unexpected value: $unexpected for Authorization: Basic"))
  }
}

object AuthServiceLive {
  lazy val layer: ZLayer[KeycloakAuthorizer with Redis, Config.Error, AuthServiceLive] = ZLayer {
    for {
      config             <- AppConfig.get
      redis              <- ZIO.service[Redis]
      keycloakAuthorizer <- ZIO.service[KeycloakAuthorizer]
    } yield AuthServiceLive(redis, config, keycloakAuthorizer)
  }
}
