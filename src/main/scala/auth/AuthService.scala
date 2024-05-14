package auth

import auth.keycloak.KeycloakAuthorizer
import auth.models.AuthResult
import scalapb.zio_grpc.RequestContext
import zio.http.{Client, Request}
import zio.macros.{accessible, throwing}
import zio.redis.Redis
import zio.{Config, RIO, Scope, ZLayer}

@accessible
trait AuthService {
  @throwing
  def validateHeader(request: Request): RIO[Scope, AuthResult]

  @throwing
  def validateContext(request: RequestContext): RIO[Scope, AuthResult]
}

object AuthService {
  val live: ZLayer[Client with Redis with KeycloakAuthorizer, Config.Error, AuthService] = AuthServiceLive.layer
}
