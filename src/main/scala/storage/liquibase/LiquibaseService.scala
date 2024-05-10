package storage.liquibase

import config.AppConfig
import liquibase.Liquibase
import storage.DB
import zio.macros.accessible
import zio.{RIO, Scope, ZLayer}

@accessible
trait LiquibaseService {
  def performMigration: RIO[Liquibase, Unit]
  def performMigrationClean: RIO[Liquibase, Unit]
  def performMigrationWithDropAll: RIO[Liquibase, Unit]
}

object LiquibaseService {

  val live: ZLayer[Any, Throwable, LiquibaseServiceLive] = LiquibaseServiceLive.layer

  val liquibaseLayer: ZLayer[Any with AppConfig with Scope, Throwable, Liquibase] =
    DB.live >>> LiquibaseServiceLive.liquibaseLayer
}
