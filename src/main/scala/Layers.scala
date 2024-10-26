import auth.AuthService
import auth.keycloak.KeycloakAuthorizer
import config.AppConfig
import cvpservice.CVPServiceEnv
import http.HTTPServer
import metrics.metricsConfig
import storage.DB
import storage.liquibase.LiquibaseService
import storage.postgres.ProfileRepository
import storage.redis._
import zio.http.Client
import zio.metrics.connectors.prometheus
import zio.{Scope, TaskLayer}

object Layers {

  private val runtime = Scope.default

  private val base = AppConfig.live

  private val database = DB.live

  private val services =
    Client.default >+>
      redisProtobufCodecLayer >+>
      HTTPServer.live >+>
      ProfileRepository.live >+>
      LiquibaseService.live >+>
      KeycloakAuthorizer.live >+>
      AuthService.live >+>
      LiquibaseService.liquibaseLayer

  private val metrics = metricsConfig >+> prometheus.publisherLayer >+> prometheus.prometheusLayer

  val all: TaskLayer[CVPServiceEnv] =
    runtime >+>
      base >+>
      database >+>
      services >+>
      metrics
}
