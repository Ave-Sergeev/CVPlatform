package mq

import config.AppConfig
import org.apache.kafka.clients.producer.RecordMetadata
import zio.kafka.producer.Producer
import zio.{RIO, RLayer}

import java.util.UUID

trait KafkaService {
  def produce(topic: String, key: UUID, value: String): RIO[Producer, RecordMetadata]
}

object KafkaService {
  val live: RLayer[AppConfig, Producer] = KafkaServiceLive.layer
}
