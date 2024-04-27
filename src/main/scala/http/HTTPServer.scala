package http

import auth.AuthService
import config.AppConfig
import http.api.{Health, Profile, TestEndpoint}
import storage.db.ProfileRepository
import zio.http.Server
import zio.http.netty.NettyConfig
import zio.http.netty.NettyConfig.LeakDetectionLevel
import zio.{TaskLayer, URIO, ZIO, ZLayer}

object HTTPServer {

  private val allRoutes = Health.routes ++ Profile.routes ++ TestEndpoint.route

  def start: URIO[AuthService with ProfileRepository with Server, Nothing] = Server.serve(allRoutes)

  private val nettyConfigLayer = ZLayer.succeed(
    NettyConfig.default
      .leakDetection(LeakDetectionLevel.DISABLED)
  )

  private val serverConfigLayer = ZLayer.fromZIO {
    ZIO.config[AppConfig](AppConfig.configDescriptor).map { config =>
      Server.Config.default.port(config.interface.httpPort)
    }
  }

  lazy val live: TaskLayer[Server] = (serverConfigLayer ++ nettyConfigLayer) >>> Server.customized
}
