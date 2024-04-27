import config.AppConfig
import http.HTTPServer
import zio.Console.printLine
import zio.Runtime.setConfigProvider
import zio.config.typesafe.TypesafeConfigProvider
import zio.{ExitCode, ZIO, ZIOAppArgs, ZIOAppDefault, ZLayer}

object Main extends ZIOAppDefault {

  override val bootstrap: ZLayer[ZIOAppArgs, Any, Any] = setConfigProvider(TypesafeConfigProvider.fromResourcePath())

  private val program = for {
    config <- AppConfig.get
    _ <- ZIO.logInfo(s"Server (HTTP) is running on port ${config.interface.httpPort}. Press Ctrl-C to stop.")
    http <- HTTPServer.start.exitCode
  } yield http

  override def run: ZIO[Any, Nothing, ExitCode] = program
    .provide(Layers.all)
    .foldZIO(
      err => printLine(s"Execution failed with: $err").exitCode,
      _ => ZIO.succeed(ExitCode.success)
    )
}
