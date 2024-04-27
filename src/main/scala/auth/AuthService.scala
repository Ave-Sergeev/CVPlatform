package auth

import zio.http.{Request, Response}
import zio.macros.accessible
import zio.{IO, TaskLayer}

@accessible
trait AuthService {
  def validateAuth(request: Request): IO[Response, Boolean]
}

object AuthService {
  val live: TaskLayer[AuthService] = AuthServiceLive.layer
}
