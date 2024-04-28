package storage

import config.AppConfig
import zio.redis._
import zio.schema.Schema
import zio.schema.codec.{BinaryCodec, JsonCodec, ProtobufCodec}
import zio.{ULayer, URLayer, ZEnvironment, ZIO, ZLayer}

package object redis {

  private def redisConnectionLayer: URLayer[AppConfig, RedisConfig] = ZLayer.fromZIO {
    ZIO.serviceWith[AppConfig](config => RedisConfig(config.redis.host, config.redis.port))
  }

  private def redisLayer: ZLayer[AppConfig with CodecSupplier with RedisConfig, RedisError, Redis with AsyncRedis] = {
    import util._

    Redis.singleNode.tap { redis: ZEnvironment[Redis] =>
      ZIO.serviceWithZIO[AppConfig] { config =>
        val login = config.redis.username match {
          case Some(username) if username.value.nonEmpty =>
            redis.get.auth(username.secretToString, config.redis.secret.secretToString)
          case _ =>
            redis.get.auth(config.redis.secret.secretToString)
        }
        login <* ZIO.logInfo("Successfully authorized with Redis")
      }
    }
  }

  def redisJsonCodecLayer: ZLayer[AppConfig, RedisError, Redis with AsyncRedis] =
    (redisConnectionLayer ++ jsonBinaryCodecLayer) >>> redisLayer

  def redisProtobufCodecLayer: ZLayer[AppConfig, RedisError, Redis] =
    (redisConnectionLayer ++ protobufCodecLayer) >>> redisLayer

  private val jsonBinaryCodecLayer: ULayer[CodecSupplier] = ZLayer.succeed[CodecSupplier](new CodecSupplier {
    override def get[A: Schema]: BinaryCodec[A] = JsonCodec.schemaBasedBinaryCodec
  })

  private val protobufCodecLayer: ULayer[CodecSupplier] = ZLayer.succeed[CodecSupplier](new CodecSupplier {
    override def get[A: Schema]: BinaryCodec[A] = ProtobufCodec.protobufCodec
  })
}
