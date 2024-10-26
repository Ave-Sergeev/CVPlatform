import auth.AuthService
import config.AppConfig
import liquibase.Liquibase
import storage.liquibase.LiquibaseService
import storage.postgres.ProfileRepository
import zio.Scope
import zio.http.{Client, Server}
import zio.metrics.connectors.prometheus.PrometheusPublisher

package object cvpservice {

  private type ServiceEnv =
    AppConfig
      with Server
      with Liquibase
      with AuthService
      with LiquibaseService
      with ProfileRepository
      with PrometheusPublisher

  type CVPServiceEnv = ServiceEnv with Scope
}
