package http

import auth.AuthService
import config.AppConfig
import http.api.{Health, Profile}
import storage.postgres.ProfileRepository
import zio.http.Server
import zio.http.netty.NettyConfig
import zio.http.netty.NettyConfig.LeakDetectionLevel
import zio.{RLayer, Scope, URIO, ZLayer}

object HTTPServer {

  private val allRoutes = Health.routes ++ Profile.routes

  def start: URIO[AuthService with Scope with ProfileRepository with Server, Nothing] = Server.serve(allRoutes)

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
