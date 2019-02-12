package com.alaphi.accountservice.kafka.client.fs2

import java.util.Properties

import cats.effect.IO
import org.apache.kafka.clients.producer.{ProducerRecord, RecordMetadata, KafkaProducer => ApacheKafkaProducer}
import org.apache.kafka.common.serialization.Serializer

trait KafkaPublisher[K, V] {
  def send(record: ProducerRecord[K,V]): IO[Option[RecordMetadata]]
}

object KafkaPublisher {

  class PublishException extends RuntimeException

  def mkKafkaPublisher[K, V](props: Properties, keySerializer: Serializer[K], valueSerializer: Serializer[V]): KafkaPublisher[K, V] =
    new KafkaPublisher[K, V] {
      val underlyingProducer = new ApacheKafkaProducer[K, V](props, keySerializer, valueSerializer)

      def send(record: ProducerRecord[K,V]): IO[Option[RecordMetadata]] =
        IO async { cb =>
          underlyingProducer.send(record, (metadata: RecordMetadata, exception: Exception) =>
            cb(
              Option(metadata)
                .toRight(Option(exception).getOrElse(new PublishException))
                .map(meta => Some(meta))
            )
          )
        }
    }

}
