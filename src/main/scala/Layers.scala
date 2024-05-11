import auth.AuthService
import auth.keycloak.KeycloakAuthorizer
import config.AppConfig
import cvpservice.CVPServiceEnv
import http.HTTPServer
import storage.DB
import storage.liquibase.LiquibaseService
import storage.postgres.ProfileRepository
import storage.redis._
import zio.http.{Client, ZClient}
import zio.{Scope, TaskLayer}

object Layers {

  private val runtime = Scope.default

  private val base = AppConfig.layer >+> DB.live

  private val services = Client.default >+> ZClient.default >+> redisProtobufCodecLayer

  val all: TaskLayer[CVPServiceEnv] =
    runtime >+>
      base >+>
      services >+>
      HTTPServer.live >+>
      ProfileRepository.live >+>
      LiquibaseService.live >+>
      KeycloakAuthorizer.live >+>
      AuthService.live >+>
      LiquibaseService.liquibaseLayer
}
