package config

import zio.Config.Secret
import zio.config.magnolia.deriveConfig
import zio.config.{toKebabCase, ConfigOps}
import zio.{Config, IO, Layer, ZIO, ZLayer}

case class AppConfig(
    redis: Redis,
    metrics: Metrics,
    keycloak: Keycloak,
    liquibase: Liquibase,
    interface: Interface,
    basicAuth: BasicAuth
)

case class Liquibase(
    changeLog: String
)

case class Metrics(
    intervalMillis: Int
)

case class Interface(
    httpPort: Int,
    grpcPort: Int,
    maxInboundMessage: Long
)

case class BasicAuth(
    login: Secret,
    password: Secret
)

case class Keycloak(
    host: String,
    realm: String,
    clientId: String,
    clientSecret: Secret
)

case class Redis(
    host: String,
    port: Int,
    username: Option[Secret],
    secret: Secret,
    databaseIndex: Int
)

object AppConfig {

  implicit val configDescriptor: Config[AppConfig] = (
    deriveConfig[Redis].nested("redis") zip
      deriveConfig[Metrics].nested("metrics") zip
      deriveConfig[Keycloak].nested("keycloak") zip
      deriveConfig[Liquibase].nested("liquibase") zip
      deriveConfig[Interface].nested("interface") zip
      deriveConfig[BasicAuth].nested("basicAuth")
  )
    .to[AppConfig]
    .mapKey(toKebabCase)

  def get: IO[Config.Error, AppConfig] = ZIO.config[AppConfig](configDescriptor)

  def get[A](f: AppConfig => A): IO[Config.Error, A] = get.map(f)

  val live: Layer[Config.Error, AppConfig] = ZLayer.fromZIO {
    ZIO.config[AppConfig](configDescriptor)
  }
}
