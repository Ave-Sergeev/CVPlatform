package http

import config.AppConfig
import cvpservice.CVPServiceEnv
import http.endpoints.{Health, Profile}
import metrics.PrometheusMetricsPublisher
import zio.http.Server
import zio.http.netty.NettyConfig
import zio.http.netty.NettyConfig.LeakDetectionLevel
import zio.{RLayer, URIO, ZLayer}

object HTTPServer {

  private val allRoutes = Health.routes ++ Profile.routes ++ PrometheusMetricsPublisher.routes

  def start: URIO[CVPServiceEnv, Nothing] = Server.serve(allRoutes)

  private val nettyConfigLayer = ZLayer.succeed(
    NettyConfig.default
      .leakDetection(LeakDetectionLevel.DISABLED)
  )

  private val serverConfigLayer = ZLayer.fromZIO {
    AppConfig.get(_.interface).map { config =>
      Server.Config.default.port(config.httpPort)
    }
  }

  lazy val live: RLayer[AppConfig, Server] = (serverConfigLayer ++ nettyConfigLayer) >>> Server.customized
}
