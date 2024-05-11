package auth

import auth.keycloak.KeycloakAuthorizer
import zio.http.{Client, Request, Response}
import zio.macros.{accessible, throwing}
import zio.redis.Redis
import zio.{Config, Scope, ZIO, ZLayer}

@accessible
trait AuthService {
  @throwing
  def validateAuth(request: Request): ZIO[Scope, Response, Boolean]
}

object AuthService {
  val live: ZLayer[Client with Redis with KeycloakAuthorizer, Config.Error, AuthService] = AuthServiceLive.layer
}
