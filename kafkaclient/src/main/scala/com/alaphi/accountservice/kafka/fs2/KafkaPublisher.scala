package com.alaphi.accountservice.kafka.fs2

import cats.effect.IO
import org.apache.kafka.clients.producer.{ProducerRecord, RecordMetadata, KafkaProducer => ApacheKafkaProducer}

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
