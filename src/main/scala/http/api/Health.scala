package http.api

import zio.http._

object Health {
  def routes: HttpApp[Any] =
    Routes(
      Method.GET / "health" -> handler(Response(Status.Ok))
    ).toHttpApp
}
