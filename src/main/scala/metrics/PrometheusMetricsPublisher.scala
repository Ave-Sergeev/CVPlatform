package metrics

import zio._
import zio.http.endpoint.openapi.OpenAPI.SecurityScheme.Http
import zio.http.{Method, _}
import zio.metrics.connectors.prometheus.PrometheusPublisher

object PrometheusMetricsPublisher {

  def routes: HttpApp[PrometheusPublisher] =
    Routes(
      Method.GET / "metrics" -> handler(
        ZIO.serviceWithZIO[PrometheusPublisher](_.get.map(Response.text))
      )
    ).toHttpApp
}
