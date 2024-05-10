package mq

import config.{AppConfig, Kafka}
import org.apache.kafka.clients.producer.RecordMetadata
import zio.kafka.producer.{Producer, ProducerSettings}
import zio.kafka.serde.Serde
import zio.{RIO, RLayer, TaskLayer, ZLayer}

import java.util.UUID

case class KafkaServiceLive() extends KafkaService {

  def produce(topic: String, key: UUID, value: String): RIO[Producer, RecordMetadata] = {
    Producer.produce[Any, UUID, String](
      topic = topic,
      key = key,
      value = value,
      keySerializer = Serde.uuid,
      valueSerializer = Serde.string
    )
  }
}

object KafkaServiceLive {

  def layer: RLayer[AppConfig, Producer] =
    ZLayer
      .service[AppConfig]
      .flatMap(config => make(config.get.kafka))

  private def make(config: Kafka): TaskLayer[Producer] = {
    val bootstrapServers = List(s"${config.host}:${config.port}")
    val producerSetting  = ProducerSettings(bootstrapServers)

    ZLayer.scoped(Producer.make(producerSetting))
  }
}
