package http.api

import zio.http.endpoint.Endpoint
import zio.http.{uuid, Handler, HttpApp, Method}

import java.util.UUID

object TestEndpoint {

  private val endpoint =
    Endpoint(Method.GET / "test" / uuid("id"))
      .out[String]

  val route: HttpApp[Any] = endpoint.implement {
    Handler.fromFunction[UUID] { id =>
      id.toString
    }
  }.toHttpApp
}
