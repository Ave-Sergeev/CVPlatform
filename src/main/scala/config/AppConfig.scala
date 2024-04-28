package config

import zio.Config.Secret
import zio.config.magnolia.deriveConfig
import zio.config.{toKebabCase, ConfigOps}
import zio.{Config, IO, ZIO}

case class AppConfig(
    interface: Interface,
    basicAuth: BasicAuth
)

case class Interface(httpPort: Int, grpcPort: Int)

case class BasicAuth(login: Secret, password: Secret)

object AppConfig {

  implicit val configDescriptor: Config[AppConfig] = {
    (
      deriveConfig[Interface].nested("interface") zip
        deriveConfig[BasicAuth].nested("basicAuth")
    )
      .to[AppConfig]
      .mapKey(toKebabCase)
  }

  def get: IO[Config.Error, AppConfig] = ZIO.config[AppConfig](configDescriptor)
}
