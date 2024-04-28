package auth

import zio.http.{Request, Response}
import zio.macros.accessible
import zio.redis.Redis
import zio.{Config, IO, ZLayer}

@accessible
trait AuthService {
  def validateAuth(request: Request): IO[Response, Boolean]
}

object AuthService {
  val live: ZLayer[Redis, Config.Error, AuthServiceLive] = AuthServiceLive.layer
}
