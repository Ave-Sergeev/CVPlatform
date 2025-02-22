package grpc

import auth.AuthService
import auth.models._
import io.grpc.{Status, StatusException}
import metrics.Counters.{countFailedGrpcRequests, countSuccessfulGrpcRequests}
import scalapb.zio_grpc.RequestContext
import util.ulid.ULID
import zio.{IO, Scope, ZEnvironment, ZIO, ZIOAspect}

package object service {

  def handleRPC[R, E <: Throwable, A](context: RequestContext)(
      effect: ZIO[R, E, A]
  )(implicit env: ZEnvironment[R with AuthService with Scope]): IO[StatusException, A] =
    (
      for {
        authResult <- AuthService
          .validateContext(context)
          .mapError(err => new StatusException(Status.UNAUTHENTICATED.withDescription(err.getMessage)))
          .logError("Unauthorized")
        traceId <- ULID.newEffectULIDString
        methodName = context.methodDescriptor.getBareMethodName
        annotations = ZIOAspect.annotated(
          "user"    -> authResult.username,
          "method"  -> methodName,
          "traceId" -> traceId
        )
        result <- authResult match {
          case ValidAuthResult(_, _) =>
            // TODO: You can add role checking if needed.
            effect
              .mapError(err => Status.fromThrowable(err).asException) @@
              countSuccessfulGrpcRequests(methodName) @@
              countFailedGrpcRequests(methodName) @@
              annotations <* ZIO.logInfo("Authorization passed") @@ annotations
          case InvalidAuthResult(_) =>
            ZIO.fail(Status.UNAUTHENTICATED.asException)
        }
      } yield result
    )
      .provideEnvironment(env)
}
