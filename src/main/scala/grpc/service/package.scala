package grpc

import auth.AuthService
import io.grpc.{Status, StatusException}
import scalapb.zio_grpc.RequestContext
import zio.{IO, ZEnvironment, ZIO}

package object service {
  def handleRPC[R, E <: Throwable, A](rc: RequestContext)(
      func: ZIO[R, E, A]
  )(implicit env: ZEnvironment[R with AuthService]): IO[StatusException, A] =
    (
      for {
        // TODO: Добавить auth + annotation + metrics
//        traceId = UUID.randomUUID.toString
//        methodName = rc.methodDescriptor.getBareMethodName
//        annotations = ZIOAspect.annotated(
//          "method" -> methodName,
//          "traceId" -> traceId,
//        )
        result <- func.mapError(err => Status.fromThrowable(err).asException)
      } yield result
    )
      .provideEnvironment(env)
}
