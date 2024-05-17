import config.AppConfig
import zio.metrics.connectors.MetricsConfig
import zio.{durationInt, TaskLayer, ZLayer}

package object metrics {

  val metricsConfig: TaskLayer[MetricsConfig] = ZLayer.fromZIO {
    AppConfig.get(_.metrics).map(config => MetricsConfig(config.intervalMillis.millis))
  }
}
