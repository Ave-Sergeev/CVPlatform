package storage.liquibase

import config.AppConfig
import liquibase.Liquibase
import liquibase.database.jvm.JdbcConnection
import liquibase.resource.{ClassLoaderResourceAccessor, CompositeResourceAccessor, FileSystemResourceAccessor}
import zio.{RIO, Scope, ULayer, URIO, ZIO, ZLayer}

import javax.sql.DataSource

final case class LiquibaseServiceLive() extends LiquibaseService {

  override def performMigration: RIO[Liquibase, Unit] = LiquibaseServiceLive.liquibase.map(_.update("dev"))

  override def performMigrationClean: RIO[Liquibase, Unit] = for {
    liquibase <- LiquibaseServiceLive.liquibase
    _         <- ZIO.from(liquibase.clearCheckSums())
    _         <- ZIO.from(liquibase.update("dev"))
  } yield ()

  override def performMigrationWithDropAll: RIO[Liquibase, Unit] = for {
    liquibase <- LiquibaseServiceLive.liquibase
    _         <- ZIO.from(liquibase.clearCheckSums())
    _         <- ZIO.from(liquibase.dropAll())
    _         <- ZIO.from(liquibase.update("dev"))
  } yield ()
}

object LiquibaseServiceLive {

  def liquibase: URIO[Liquibase, Liquibase] = ZIO.service[Liquibase]

  def layer: ULayer[LiquibaseServiceLive] = ZLayer.succeed(LiquibaseServiceLive())

  def liquibaseLayer: ZLayer[Scope with DataSource with AppConfig, Throwable, Liquibase] =
    ZLayer.fromZIO(
      for {
        config    <- AppConfig.get
        liquibase <- mkLiquibase(config)
      } yield liquibase
    )

  private def mkLiquibase(config: AppConfig): ZIO[Scope with DataSource, Throwable, Liquibase] = for {
    ds                  <- ZIO.environment[DataSource].map(_.get)
    fileAccessor        <- ZIO.from(new FileSystemResourceAccessor())
    classLoader         <- ZIO.from(classOf[LiquibaseService].getClassLoader)
    classLoaderAccessor <- ZIO.from(new ClassLoaderResourceAccessor(classLoader))
    fileOpener          <- ZIO.from(new CompositeResourceAccessor(fileAccessor, classLoaderAccessor))
    jdbcConn            <- ZIO.acquireRelease(ZIO.from(new JdbcConnection(ds.getConnection)))(c => ZIO.succeed(c.close()))
    liquibase           <- ZIO.from(new Liquibase(config.liquibase.changeLog, fileOpener, jdbcConn))
  } yield liquibase
}
