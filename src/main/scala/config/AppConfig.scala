package config

import zio.Config.Secret
import zio.config.magnolia.deriveConfig
import zio.config.{toKebabCase, ConfigOps}
import zio.{Config, IO, Layer, ZIO, ZLayer}

case class AppConfig(
    interface: Interface,
    basicAuth: BasicAuth,
    redis: Redis,
    liquibase: Liquibase,
    keycloak: Keycloak,
    metrics: Metrics
)

case class Liquibase(changeLog: String)

case class Metrics(intervalMillis: Int)

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

object AppConfig {

  implicit val configDescriptor: Config[AppConfig] = {
    (
      deriveConfig[Interface].nested("interface") zip
        deriveConfig[BasicAuth].nested("basicAuth") zip
        deriveConfig[Redis].nested("redis") zip
        deriveConfig[Liquibase].nested("liquibase") zip
        deriveConfig[Keycloak].nested("keycloak") zip
        deriveConfig[Metrics].nested("metrics")
    )
      .to[AppConfig]
      .mapKey(toKebabCase)
  }

  def get: IO[Config.Error, AppConfig] = ZIO.config[AppConfig](configDescriptor)

  def get[A](f: AppConfig => A): IO[Config.Error, A] = get.map(f)

  val layer: Layer[Config.Error, AppConfig] = ZLayer.fromZIO {
    ZIO.config[AppConfig](configDescriptor)
  }
}
