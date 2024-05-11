import config.AppConfig
import http.HTTPServer
import storage.liquibase.LiquibaseService
import zio.Console.printLine
import zio.Runtime.setConfigProvider
import zio.config.typesafe.TypesafeConfigProvider
import zio.logging.backend.SLF4J
import zio.{ExitCode, UIO, ZIO, ZIOAppArgs, ZIOAppDefault, ZLayer}

object Main extends ZIOAppDefault {

  override val bootstrap: ZLayer[ZIOAppArgs, Any, Any] =
    zio.Runtime.removeDefaultLoggers >>>
      SLF4J.slf4j >>>
      setConfigProvider(TypesafeConfigProvider.fromResourcePath())

  private val program = for {
    config <- AppConfig.get(_.interface)
    _      <- ZIO.logInfo(s"Server (HTTP) is running on port ${config.httpPort}. Press Ctrl-C to stop.")
    http   <- LiquibaseService.performMigration *> HTTPServer.start.exitCode
  } yield http

  override def run: UIO[ExitCode] = program
    .provide(Layers.all)
    .foldZIO(
      err => printLine(s"Execution failed with: $err").exitCode,
      _ => ZIO.succeed(ExitCode.success)
    )
}
