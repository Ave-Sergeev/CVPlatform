package auth.keycloak

import auth.models.{IntrospectResponse, User}
import zio.http.Client
import zio.macros.{accessible, throwing}
import zio.redis.Redis
import zio.{Config, RIO, Scope, ZLayer}

@accessible
trait KeycloakAuthorizer {
  @throwing
  def userInfo(token: String): RIO[Scope, User]
  @throwing
  def introspectToken(token: String): RIO[Scope, IntrospectResponse]
}

object KeycloakAuthorizer {
  val live: ZLayer[Client with Redis, Config.Error, KeycloakAuthorizer] =
    KeycloakAuthorizerLive.layer
}
