package storage

import config.AppConfig
import zio.redis._
import zio.schema.Schema
import zio.schema.codec.{BinaryCodec, JsonCodec, ProtobufCodec}
import zio.{redis, ULayer, ZEnvironment, ZIO, ZLayer}

package object redis {
  import util._

  private def redisConnection: ZLayer[CodecSupplier with AppConfig, RedisError, Redis] =
    ZLayer
      .service[AppConfig]
      .flatMap { config =>
        val redisConfig = config.get.redis
        ZLayer.succeed(redis.RedisConfig(redisConfig.host, redisConfig.port)) >>> Redis.singleNode
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

  private val jsonBinaryCodecLayer: ULayer[CodecSupplier] = ZLayer.succeed[CodecSupplier](new CodecSupplier {
    override def get[A: Schema]: BinaryCodec[A] = JsonCodec.schemaBasedBinaryCodec
  })

  private val protobufCodecLayer: ULayer[CodecSupplier] = ZLayer.succeed[CodecSupplier](new CodecSupplier {
    override def get[A: Schema]: BinaryCodec[A] = ProtobufCodec.protobufCodec
  })
}
