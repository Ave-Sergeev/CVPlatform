package grpc

import config.AppConfig
import cvpservice.CVPServiceEnv
import grpc.service.HelloController
import io.grpc.netty.NettyServerBuilder
import scalapb.zio_grpc.{ScopedServer, Server, ServiceList}
import zio.{RIO, ZIO}

object GrpcService {
  final def startServer: RIO[CVPServiceEnv, Server] = {
    for {
      cfg         <- AppConfig.get(_.interface)
      controllers <- ZIO.attempt(ServiceList.addZIO(HelloController.make))
      build = NettyServerBuilder
        .forPort(cfg.grpcPort)
        .maxInboundMessageSize(cfg.maxInboundMessage.toInt)
      server <- ScopedServer.fromServiceList(build, controllers)
    } yield server
  } *> ZIO.never
}
