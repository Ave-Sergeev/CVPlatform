import auth.AuthService
import config.AppConfig
import http.HTTPServer
import storage.liquibase.LiquibaseService
import storage.postgres.ProfileRepository
import storage.redis._
import zio.Scope
import zio.http.Client

object Layers {

  private val runtime = Scope.default

  private val base = AppConfig.layer

  private val services =
    (redisProtobufCodecLayer >>> AuthService.live) >+>
      HTTPServer.live >+>
      Client.default >+>
      ProfileRepository.live >+>
      LiquibaseService.live >+>
      LiquibaseService.liquibaseLayer

  val all =
    runtime >+>
      base >+>
      services
}
