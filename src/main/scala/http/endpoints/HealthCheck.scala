package http.endpoints

import zio.http._

object HealthCheck {
  val routes: Routes[Any, Nothing] = Routes(
    Method.GET / "healthCheck" -> Handler.ok
  )
}
