package config

import zio.Config.Secret

case class RedisConfig(
    username: Option[Secret],
    secret: Secret,
    entryLifeDays: Int,
    requestQueueSize: Option[Int],
    deployment: RedisConfig.DeploymentModel
)

object RedisConfig {
  sealed trait DeploymentModel

  case class Single(
      host: String,
      port: Int,
      ssl: Option[Boolean],
      sni: Option[String]
  ) extends DeploymentModel

  case class Cluster(
      nodes: List[Single]
  ) extends DeploymentModel
}
