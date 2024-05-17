package metrics

import zio.ZIOAspect
import zio.metrics.{Metric, MetricLabel}

object Counters {

  def countSuccessfulGrpcRequests(methodName: String): ZIOAspect[Nothing, Any, Nothing, Any, Nothing, Any] =
    Metric
      .counter("successGrpcRequests")
      .fromConst(1)
      .tagged(
        MetricLabel("method", methodName)
      )
      .trackSuccess

  def countFailedGrpcRequests(methodName: String): ZIOAspect[Nothing, Any, Nothing, Any, Nothing, Any] =
    Metric
      .counter("failedGrpcRequests")
      .fromConst(1)
      .tagged(
        MetricLabel("method", methodName)
      )
      .trackError

  def countRESTRequests(path: String): ZIOAspect[Nothing, Any, Nothing, Any, Nothing, Any] =
    counterPair("RESTRequests", "method" -> path)

  private def counterPair(name: String, labels: (String, String)*): ZIOAspect[Nothing, Any, Nothing, Any, Nothing, Any] = {
    val base = Metric
      .counter(name)
      .fromConst(1)
      .tagged(
        labels.map { case (key, value) => MetricLabel(key, value) }.toSet
      )
    base.tagged("result", "SUCCEED").trackSuccess @@
      base.tagged("result", "FAILED").trackError
  }
}
