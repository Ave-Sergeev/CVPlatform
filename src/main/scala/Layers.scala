import auth.AuthService
import config.AppConfig
import http.HTTPServer
import storage.postgres.ProfileRepository
import storage.redis._
import zio.Scope

object Layers {

  private val runtime = Scope.default

  private val base = AppConfig.layer

  private val services =
    (redisProtobufCodecLayer >>> AuthService.live) >+>
      HTTPServer.live >+>
      ProfileRepository.live

  val all =
    runtime >+>
      base >+>
      services
}
