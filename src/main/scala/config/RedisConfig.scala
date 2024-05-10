package config

import zio.Config.Secret

case class Redis(
    host: String,
    port: Int,
    username: Option[Secret],
    secret: Secret
)
