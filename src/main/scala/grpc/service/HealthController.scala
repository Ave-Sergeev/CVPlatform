package grpc.service

import health.HealthCheckResponse.ServingStatus
import health.ZioHealth.RCHealth
import health.{HealthCheckRequest, HealthCheckResponse}
import io.grpc.{Status, StatusException}
import scalapb.zio_grpc.RequestContext
import zio.stream.ZStream
import zio.{durationInt, stream, IO, UIO, ZIO}

// TODO: This implementation of healthCheck for gRPC (with this .proto file) is necessary for livenessProbe to work correctly in helm
final case class HealthController() extends RCHealth {

  override def check(request: HealthCheckRequest, context: RequestContext): IO[StatusException, HealthCheckResponse] =
    ZIO.attempt(HealthCheckResponse(ServingStatus.SERVING)).orElseFail(Status.INTERNAL.asException)

  override def watch(request: HealthCheckRequest, context: RequestContext): stream.Stream[StatusException, HealthCheckResponse] =
    ZStream
      .tick(1.second)
      .as(HealthCheckResponse(ServingStatus.SERVING))
}

object HealthController {
  def make: UIO[HealthController] = ZIO.succeed(HealthController())
}
