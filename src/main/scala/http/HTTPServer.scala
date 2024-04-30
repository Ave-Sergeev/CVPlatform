package http

import auth.AuthService
import config.AppConfig
import http.api.{Health, Profile, TestEndpoint}
import storage.postgres.ProfileRepository
import zio.http.Server
import zio.http.netty.NettyConfig
import zio.http.netty.NettyConfig.LeakDetectionLevel
import zio.{Scope, URIO, ZIO, ZLayer}

object HTTPServer {

  private val allRoutes = Health.routes ++ Profile.routes ++ TestEndpoint.route

  def start: URIO[AuthService with Scope with ProfileRepository with Server, Nothing] = Server.serve(allRoutes)

  private val nettyConfigLayer = ZLayer.succeed(
    NettyConfig.default
      .leakDetection(LeakDetectionLevel.DISABLED)
  )

  private val serverConfigLayer = ZLayer.fromZIO {
    ZIO.serviceWith[AppConfig] { config =>
      Server.Config.default.port(config.interface.httpPort)
    }
  }

  lazy val live: ZLayer[AppConfig, Throwable, Server] = (serverConfigLayer ++ nettyConfigLayer) >>> Server.customized
}
