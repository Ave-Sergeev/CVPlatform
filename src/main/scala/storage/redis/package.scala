package storage

import config.AppConfig
import config.RedisConfig._
import zio.redis._
import zio.redis.embedded.EmbeddedRedis
import zio.schema.Schema
import zio.schema.codec.{BinaryCodec, JsonCodec, ProtobufCodec}
import zio.{redis, Chunk, TaskLayer, ULayer, ZEnvironment, ZIO, ZLayer}

package object redis {
  import util._

  private def redisConnection: ZLayer[CodecSupplier with AppConfig, RedisError, Redis] =
    ZLayer
      .service[AppConfig]
      .flatMap { config =>
        val redisConfig      = config.get.redis
        val requestQueueSize = redisConfig.requestQueueSize.getOrElse(16)
        redisConfig.deployment match {
          case Single(host, port, ssl, sni) =>
            ZLayer.succeed(redis.RedisConfig(host, port, sni, ssl.getOrElse(false), requestQueueSize)) >>> Redis.singleNode
          case Cluster(nodes) =>
            val nodesConfig = Chunk.fromIterable(nodes.map(node => RedisUri(node.host, node.port, node.ssl.getOrElse(false), node.sni)))
            ZLayer.succeed(RedisClusterConfig(nodesConfig, requestQueueSize = requestQueueSize)) >>> Redis.cluster
        }
      }
      .tap { redis: ZEnvironment[Redis] =>
        ZIO.serviceWithZIO[AppConfig] { config =>
          val login = config.redis.username match {
            case Some(username) if username.secretToString.nonEmpty => redis.get.auth(username.secretToString, config.redis.secret.secretToString)
            case _                                                  => redis.get.auth(config.redis.secret.secretToString)
          }
          login <* ZIO.logInfo("Successfully authorized with Redis")
        }
      }

  def redisJsonCodecLayer: ZLayer[AppConfig, RedisError, Redis] =
    jsonBinaryCodecLayer >>> redisConnection

  def redisProtobufCodecLayer: ZLayer[AppConfig, RedisError, Redis] =
    protobufCodecLayer >>> redisConnection

  def redisJsonTestLayer: TaskLayer[Redis] =
    (jsonBinaryCodecLayer ++ EmbeddedRedis.layer) >>> Redis.singleNode

  def redisProtobufTestLayer: TaskLayer[Redis] =
    (protobufCodecLayer ++ EmbeddedRedis.layer) >>> Redis.singleNode

  private val jsonBinaryCodecLayer: ULayer[CodecSupplier] = ZLayer.succeed[CodecSupplier](new CodecSupplier {
    override def get[A: Schema]: BinaryCodec[A] = JsonCodec.schemaBasedBinaryCodec
  })

  private val protobufCodecLayer: ULayer[CodecSupplier] = ZLayer.succeed[CodecSupplier](new CodecSupplier {
    override def get[A: Schema]: BinaryCodec[A] = ProtobufCodec.protobufCodec
  })
}
