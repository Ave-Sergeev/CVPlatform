import sbt.*

object Dependencies {

  object Version {
    val scala        = "2.13.16"
    val zio          = "2.1.15"
    val zioJson      = "0.7.26"
    val zioHttp      = "3.0.1"
    val zioRedis     = "1.0.1"
    val zioConfig    = "4.0.3"
    val zioSchema    = "1.6.3"
    val zioLogging   = "2.4.0"
    val zioMetrics   = "2.3.1"
    val netty        = "4.1.118.Final"
    val quill        = "4.8.5"
    val postgre      = "42.7.5"
    val sl4j         = "2.0.16"
    val ulid         = "2025.1.6"
    val grpc         = "1.70.0"
    val logback      = "1.5.16"
    val scalaLogging = "3.9.5"
    val liquibase    = "3.4.2"
    val silencer     = "1.17.13"
  }

  object ZIO {
    lazy val core              = "dev.zio" %% "zio"                               % Version.zio
    lazy val json              = "dev.zio" %% "zio-json"                          % Version.zioJson
    lazy val redis             = "dev.zio" %% "zio-redis"                         % Version.zioRedis
    lazy val macros            = "dev.zio" %% "zio-macros"                        % Version.zio
    lazy val schema            = "dev.zio" %% "zio-schema"                        % Version.zioSchema
    lazy val metrics           = "dev.zio" %% "zio-metrics-connectors"            % Version.zioMetrics
    lazy val schemaPb          = "dev.zio" %% "zio-schema-protobuf"               % Version.zioSchema
    lazy val schemaJson        = "dev.zio" %% "zio-schema-json"                   % Version.zioSchema
    lazy val redisEmbedded     = "dev.zio" %% "zio-redis-embedded"                % Version.zioRedis
    lazy val metricsPrometheus = "dev.zio" %% "zio-metrics-connectors-prometheus" % Version.zioMetrics
  }

  object HTTP {
    lazy val zioHttp   = "dev.zio" %% "zio-http"  % Version.zioHttp
    lazy val httpNetty = "io.netty" % "netty-all" % Version.netty
  }

  object CONFIG {
    lazy val core     = "dev.zio" %% "zio-config"          % Version.zioConfig
    lazy val refined  = "dev.zio" %% "zio-config-refined"  % Version.zioConfig
    lazy val magnolia = "dev.zio" %% "zio-config-magnolia" % Version.zioConfig
    lazy val typesafe = "dev.zio" %% "zio-config-typesafe" % Version.zioConfig
  }

  object STORAGE {
    lazy val quill     = "io.getquill"   %% "quill-jdbc-zio" % Version.quill
    lazy val postgre   = "org.postgresql" % "postgresql"     % Version.postgre
    lazy val liquibase = "org.liquibase"  % "liquibase-core" % Version.liquibase
  }

  object LOGS {
    lazy val sl4j             = "org.slf4j"                   % "slf4j-api"          % Version.sl4j
    lazy val logback          = "ch.qos.logback"              % "logback-classic"    % Version.logback
    lazy val zioLogging       = "dev.zio"                    %% "zio-logging"        % Version.zioLogging
    lazy val scalaLogging     = "com.typesafe.scala-logging" %% "scala-logging"      % Version.scalaLogging
    lazy val zioLoggingSlf4j2 = "dev.zio"                    %% "zio-logging-slf4j2" % Version.zioLogging
  }

  object GRPC {
    lazy val core           = "com.thesamet.scalapb"               %% "scalapb-runtime-grpc"                    % scalapb.compiler.Version.scalapbVersion
    lazy val grpclb         = "io.grpc"                             % "grpc-grpclb"                             % Version.grpc
    lazy val grpcNetty      = "io.grpc"                             % "grpc-netty"                              % Version.grpc
    lazy val annotation     = "com.thesamet.scalapb"               %% "scalapb-runtime"                         % scalapb.compiler.Version.scalapbVersion % "protobuf"
    lazy val commonProtos   = "com.thesamet.scalapb.common-protos" %% "proto-google-common-protos-scalapb_0.11" % "2.9.6-0"
    lazy val commonProtosPb = "com.thesamet.scalapb.common-protos" %% "proto-google-common-protos-scalapb_0.11" % "2.9.6-0"                               % "protobuf"
  }

  object COMPILER {
    lazy val betterMonadicFor = compilerPlugin("com.olegpy" %% "better-monadic-for" % "0.3.1")
    lazy val silencerLib      = "com.github.ghik" % "silencer-lib"    % Version.silencer % Provided cross CrossVersion.full
    lazy val silencerPlugin   = "com.github.ghik" % "silencer-plugin" % Version.silencer cross CrossVersion.full
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
    LOGS.sl4j,
    LOGS.logback,
    LOGS.zioLogging,
    LOGS.scalaLogging,
    LOGS.zioLoggingSlf4j2,
    HTTP.zioHttp,
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
