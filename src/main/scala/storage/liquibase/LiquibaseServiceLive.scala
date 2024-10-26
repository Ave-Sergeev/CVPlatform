package storage.liquibase

import config.{AppConfig, Liquibase => LiquibaseConfig}
import liquibase.Liquibase
import liquibase.database.jvm.JdbcConnection
import liquibase.resource.{ClassLoaderResourceAccessor, CompositeResourceAccessor, FileSystemResourceAccessor}
import zio.{RIO, RLayer, Scope, ULayer, ZIO, ZLayer}

import javax.sql.DataSource

case class LiquibaseServiceLive() extends LiquibaseService {

  override def performMigration: RIO[Liquibase, Unit] = ZIO.serviceWith[Liquibase](_.update("dev"))

  override def performMigrationClean: RIO[Liquibase, Unit] =
    for {
      liquibase <- ZIO.service[Liquibase]
      _         <- ZIO.from(liquibase.clearCheckSums())
      _         <- ZIO.from(liquibase.update("dev"))
    } yield ()

  override def performMigrationWithDropAll: RIO[Liquibase, Unit] =
    for {
      liquibase <- ZIO.service[Liquibase]
      _         <- ZIO.from(liquibase.clearCheckSums())
      _         <- ZIO.from(liquibase.dropAll())
      _         <- ZIO.from(liquibase.update("dev"))
    } yield ()
}

object LiquibaseServiceLive {

  def layer: ULayer[LiquibaseService] = ZLayer.succeed(LiquibaseServiceLive())

  def liquibaseLayer: RLayer[Scope with DataSource, Liquibase] =
    ZLayer.fromZIO(
      for {
        config    <- AppConfig.get(_.liquibase)
        liquibase <- make(config)
      } yield liquibase
    )

  private def make(config: LiquibaseConfig): RIO[Scope with DataSource, Liquibase] =
    for {
      ds                  <- ZIO.environment[DataSource].map(_.get)
      fileAccessor        <- ZIO.from(new FileSystemResourceAccessor())
      classLoader         <- ZIO.from(classOf[LiquibaseService].getClassLoader)
      classLoaderAccessor <- ZIO.from(new ClassLoaderResourceAccessor(classLoader))
      fileOpener          <- ZIO.from(new CompositeResourceAccessor(fileAccessor, classLoaderAccessor))
      jdbcConn            <- ZIO.acquireRelease(ZIO.from(new JdbcConnection(ds.getConnection)))(c => ZIO.succeed(c.close()))
      liquibase           <- ZIO.from(new Liquibase(config.changeLog, fileOpener, jdbcConn))
    } yield liquibase
}
