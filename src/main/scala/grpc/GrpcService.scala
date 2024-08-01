package grpc

import config.AppConfig
import cvpservice.CVPServiceEnv
import grpc.service.{HealthController, ProfileController}
import io.grpc.netty.NettyServerBuilder
import scalapb.zio_grpc.{ScopedServer, Server, ServiceList}
import zio.{RIO, ZIO}

object GrpcService {

  final def startServer: RIO[CVPServiceEnv, Server] = {
    for {
      cfg <- AppConfig.get(_.interface)
      controllers <- ZIO.attempt(
        ServiceList
          .addZIO(HealthController.make)
          .addZIO(ProfileController.make)
      )
      build = NettyServerBuilder
        .forPort(cfg.grpcPort)
        .maxInboundMessageSize(cfg.maxInboundMessage.toInt)
      server <- ScopedServer.fromServiceList(build, controllers)
    } yield server
  } *> ZIO.never
}
