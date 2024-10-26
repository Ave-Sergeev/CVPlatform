package http.endpoints

import zio._
import zio.http.Method.GET
import zio.http._
import zio.metrics.connectors.prometheus.PrometheusPublisher

object PrometheusMetricsPublisher {
  val routes: Routes[PrometheusPublisher, Nothing] = Routes(
    GET / "metrics" -> Handler.fromZIO(ZIO.serviceWithZIO[PrometheusPublisher](_.get.map(Response.text)))
  )
}
