package auth

import zio.http.{Request, Response}
import zio.macros.accessible
import zio.redis.Redis
import zio.{Scope, ZIO, ZLayer}

@accessible
trait AuthService {
  def validateAuth(request: Request): ZIO[Scope, Response, Boolean]
}

object AuthService {
  val live: ZLayer[Redis, Throwable, AuthService] = AuthServiceLive.layer
}
