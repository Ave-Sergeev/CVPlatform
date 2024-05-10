import sbt.*

object Dependencies {

  object Version {
    val scala        = "2.13.11"
    val zio          = "2.0.22"
    val zioJson      = "0.6.2"
    val zioHttp      = "3.0.0-RC6"
    val zioRedis     = "0.2.0+89-2715ae5b-SNAPSHOT"
    val zioLogging   = "2.2.3"
    val zioSchema    = "1.1.1"
    val netty        = "4.1.94.Final"
    val kafka        = "2.7.4"
    val config       = "4.0.1"
    val quill        = "4.8.3"
    val postgre      = "42.7.3"
    val sl4j         = "2.0.12"
    val logback      = "1.5.6"
    val scalaLogging = "3.9.5"
    val liquibase    = "3.4.2"
  }

  object ZIO {
    lazy val core          = "dev.zio" %% "zio"                % Version.zio
    lazy val macros        = "dev.zio" %% "zio-macros"         % Version.zio
    lazy val json          = "dev.zio" %% "zio-json"           % Version.zioJson
    lazy val schema        = "dev.zio" %% "zio-schema"         % Version.zioSchema
    lazy val schemaJson    = "dev.zio" %% "zio-schema-json"    % Version.zioSchema
    lazy val redis         = "dev.zio" %% "zio-redis"          % Version.zioRedis
    lazy val redisEmbedded = "dev.zio" %% "zio-redis-embedded" % Version.zioRedis
  }

  object HTTP {
    lazy val zhttp     = "dev.zio" %% "zio-http"  % Version.zioHttp
    lazy val httpNetty = "io.netty" % "netty-all" % Version.netty
  }

  object CONFIG {
    lazy val core     = "dev.zio" %% "zio-config"          % Version.config
    lazy val magnolia = "dev.zio" %% "zio-config-magnolia" % Version.config
    lazy val typesafe = "dev.zio" %% "zio-config-typesafe" % Version.config
    lazy val refined  = "dev.zio" %% "zio-config-refined"  % Version.config
  }

  object DATABASE {
    lazy val quill     = "io.getquill"   %% "quill-jdbc-zio" % Version.quill
    lazy val postgre   = "org.postgresql" % "postgresql"     % Version.postgre
    lazy val liquibase = "org.liquibase"  % "liquibase-core" % Version.liquibase
  }

  object LOGS {
    lazy val core           = "ch.qos.logback"              % "logback-classic"    % Version.logback
    lazy val scalaLogging   = "com.typesafe.scala-logging" %% "scala-logging"      % Version.scalaLogging
    lazy val sl4j           = "org.slf4j"                   % "slf4j-api"          % Version.sl4j
    lazy val zioLogging     = "dev.zio"                    %% "zio-logging"        % Version.zioLogging
    lazy val zioLoggingLf4j = "dev.zio"                    %% "zio-logging-slf4j2" % Version.zioLogging
  }

  object MESSAGEBROKER {
    val kafka = "dev.zio" %% "zio-kafka" % Version.kafka
  }

  lazy val globalProjectDependencies = Seq(
    ZIO.core,
    ZIO.json,
    ZIO.macros,
    ZIO.schema,
    ZIO.schemaJson,
    ZIO.redis,
    ZIO.redisEmbedded,
    LOGS.core,
    LOGS.sl4j,
    LOGS.scalaLogging,
    LOGS.zioLogging,
    LOGS.zioLoggingLf4j,
    HTTP.zhttp,
    HTTP.httpNetty,
    CONFIG.core,
    CONFIG.magnolia,
    CONFIG.typesafe,
    CONFIG.refined,
    DATABASE.quill,
    DATABASE.postgre,
    DATABASE.liquibase,
    MESSAGEBROKER.kafka
  )
}
