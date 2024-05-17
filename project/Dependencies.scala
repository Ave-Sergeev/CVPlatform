import sbt.*

object Dependencies {

  object Version {
    val scala        = "2.13.11"
    val zio          = "2.1.1"
    val zioJson      = "0.6.2"
    val zioHttp      = "3.0.0-RC6"
    val zioRedis     = "0.2.0+89-2715ae5b-SNAPSHOT"
    val zioSchema    = "1.1.1"
    val zioLogging   = "2.2.3"
    val zioMetrics   = "2.3.1"
    val netty        = "4.1.94.Final"
    val kafka        = "2.7.4"
    val config       = "4.0.2"
    val quill        = "4.8.3"
    val postgre      = "42.7.3"
    val sl4j         = "2.0.12"
    val ulid         = "24.5.0"
    val grpc         = "1.62.2"
    val logback      = "1.5.6"
    val scalaLogging = "3.9.5"
    val liquibase    = "3.4.2"
    val silencer     = "1.7.14"
  }

  object ZIO {
    lazy val core              = "dev.zio" %% "zio"                               % Version.zio
    lazy val macros            = "dev.zio" %% "zio-macros"                        % Version.zio
    lazy val json              = "dev.zio" %% "zio-json"                          % Version.zioJson
    lazy val redis             = "dev.zio" %% "zio-redis"                         % Version.zioRedis
    lazy val redisEmbedded     = "dev.zio" %% "zio-redis-embedded"                % Version.zioRedis
    lazy val schema            = "dev.zio" %% "zio-schema"                        % Version.zioSchema
    lazy val schemaJson        = "dev.zio" %% "zio-schema-json"                   % Version.zioSchema
    lazy val schemaPb          = "dev.zio" %% "zio-schema-protobuf"               % Version.zioSchema
    lazy val metrics           = "dev.zio" %% "zio-metrics-connectors"            % Version.zioMetrics
    lazy val metricsPrometheus = "dev.zio" %% "zio-metrics-connectors-prometheus" % Version.zioMetrics
  }

  object HTTP {
    lazy val zhttp     = "dev.zio" %% "zio-http"  % Version.zioHttp
    lazy val httpNetty = "io.netty" % "netty-all" % Version.netty
  }

  object CONFIG {
    lazy val core     = "dev.zio" %% "zio-config"          % Version.config
    lazy val refined  = "dev.zio" %% "zio-config-refined"  % Version.config
    lazy val magnolia = "dev.zio" %% "zio-config-magnolia" % Version.config
    lazy val typesafe = "dev.zio" %% "zio-config-typesafe" % Version.config
  }

  object STORAGE {
    lazy val quill     = "io.getquill"   %% "quill-jdbc-zio" % Version.quill
    lazy val postgre   = "org.postgresql" % "postgresql"     % Version.postgre
    lazy val liquibase = "org.liquibase"  % "liquibase-core" % Version.liquibase
  }

  object LOGS {
    lazy val core           = "ch.qos.logback"              % "logback-classic"    % Version.logback
    lazy val sl4j           = "org.slf4j"                   % "slf4j-api"          % Version.sl4j
    lazy val zioLogging     = "dev.zio"                    %% "zio-logging"        % Version.zioLogging
    lazy val zioLoggingLf4j = "dev.zio"                    %% "zio-logging-slf4j2" % Version.zioLogging
    lazy val scalaLogging   = "com.typesafe.scala-logging" %% "scala-logging"      % Version.scalaLogging
  }

  object GRPC {
    lazy val core           = "com.thesamet.scalapb"               %% "scalapb-runtime-grpc"                    % scalapb.compiler.Version.scalapbVersion
    lazy val grpcNetty      = "io.grpc"                             % "grpc-netty"                              % Version.grpc
    lazy val grpclb         = "io.grpc"                             % "grpc-grpclb"                             % Version.grpc
    lazy val annotation     = "com.thesamet.scalapb"               %% "scalapb-runtime"                         % scalapb.compiler.Version.scalapbVersion % "protobuf"
    lazy val commonProtos   = "com.thesamet.scalapb.common-protos" %% "proto-google-common-protos-scalapb_0.11" % "2.9.6-0"
    lazy val commonProtosPb = "com.thesamet.scalapb.common-protos" %% "proto-google-common-protos-scalapb_0.11" % "2.9.6-0"                               % "protobuf"
  }

  object UTILS {
    lazy val ulid = "org.wvlet.airframe" %% "airframe-ulid" % Version.ulid
  }

  lazy val globalProjectDependencies = Seq(
    ZIO.core,
    ZIO.json,
    ZIO.redis,
    ZIO.macros,
    ZIO.schema,
    ZIO.metrics,
    ZIO.schemaPb,
    ZIO.schemaJson,
    ZIO.redisEmbedded,
    ZIO.metricsPrometheus,
    GRPC.core,
    GRPC.grpclb,
    GRPC.grpcNetty,
    GRPC.annotation,
    GRPC.commonProtos,
    GRPC.commonProtosPb,
    LOGS.core,
    LOGS.sl4j,
    LOGS.zioLogging,
    LOGS.scalaLogging,
    LOGS.zioLoggingLf4j,
    HTTP.zhttp,
    HTTP.httpNetty,
    UTILS.ulid,
    CONFIG.core,
    CONFIG.refined,
    CONFIG.typesafe,
    CONFIG.magnolia,
    STORAGE.quill,
    STORAGE.postgre,
    STORAGE.liquibase
  )
}
