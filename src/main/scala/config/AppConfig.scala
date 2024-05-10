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
    kafka: Kafka
)

case class Kafka(host: String, port: String)

case class Liquibase(changeLog: String)

case class Interface(httpPort: Int, grpcPort: Int)

case class BasicAuth(login: Secret, password: Secret)

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
        deriveConfig[Kafka].nested("kafka")
    )
      .to[AppConfig]
      .mapKey(toKebabCase)
  }

  def get: IO[Config.Error, AppConfig] = ZIO.config[AppConfig](configDescriptor)

  val layer: Layer[Config.Error, AppConfig] = ZLayer.fromZIO {
    ZIO.config[AppConfig](configDescriptor)
  }
}
