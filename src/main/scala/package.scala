import auth.AuthService
import config.AppConfig
import liquibase.Liquibase
import storage.liquibase.LiquibaseService
import storage.postgres.ProfileRepository
import zio.Scope
import zio.http.{Client, Server}

package object cvpservice {

  private type ServiceEnv =
    AppConfig
      with Server
      with Liquibase
      with AuthService
      with LiquibaseService
      with ProfileRepository

  type CVPServiceEnv = Scope with Client with ServiceEnv
}
