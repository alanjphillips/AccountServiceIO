package com.alaphi.accountservice.kafka.fs2

import java.util.Properties

import cats.effect.IO
import org.apache.kafka.clients.producer.{ProducerRecord, RecordMetadata, KafkaProducer => ApacheKafkaProducer}
import org.apache.kafka.common.serialization.Serializer

class KafkaPublisher[K, V](producer: ApacheKafkaProducer[K, V]) {

  def send(record: ProducerRecord[K,V]): IO[Option[RecordMetadata]] =
    IO async { cb =>
      producer.send(record, (metadata: RecordMetadata, exception: Exception) =>
        cb(
          Option(metadata)
            .toRight(exception)
            .map(meta => Some(meta))
        )
      )
    }

}

object KafkaPublisher {

  def apply[K, V](props: Properties, keySerializer: Serializer[K], valueSerializer: Serializer[V]): KafkaPublisher[K, V] =
    new KafkaPublisher(
      new ApacheKafkaProducer[K, V](props, keySerializer, valueSerializer)
    )

}
