package storage.liquibase

import liquibase.Liquibase
import zio.macros.accessible
import zio.{RIO, RLayer, Scope, ULayer}

import javax.sql.DataSource

@accessible
trait LiquibaseService {
  def performMigration: RIO[Liquibase, Unit]
  def performMigrationClean: RIO[Liquibase, Unit]
  def performMigrationWithDropAll: RIO[Liquibase, Unit]
}

object LiquibaseService {

  val live: ULayer[LiquibaseService] = LiquibaseServiceLive.layer

  val liquibaseLayer: RLayer[Scope with DataSource, Liquibase] = LiquibaseServiceLive.liquibaseLayer
}
