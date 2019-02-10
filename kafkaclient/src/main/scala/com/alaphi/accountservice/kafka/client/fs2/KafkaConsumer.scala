package com.alaphi.accountservice.kafka.client.fs2

import java.util.Properties

import fs2._
import cats.effect._

import scala.collection.JavaConverters._
import org.apache.kafka.clients.consumer.{ConsumerRecords, KafkaConsumer => ApacheKafkaConsumer}
import org.apache.kafka.common.serialization.Deserializer

trait KafkaConsumer[K, V] {
  def subscribe(topics: Seq[String]): Stream[IO, V]
}

object KafkaConsumer {

  def mkKafkaConsumer[K, V](props: Properties, keyDeserializer: Deserializer[K], valueDeserializer: Deserializer[V]): KafkaConsumer[K, V] =
    new KafkaConsumer[K, V] {
      val underlyingConsumer = new ApacheKafkaConsumer[K, V](props, keyDeserializer, valueDeserializer)

      override def subscribe(topics: Seq[String]): Stream[IO, V] =
        Stream.eval_(subscribeViaKafkaConsumer(topics)) ++ pollStream

      private def subscribeViaKafkaConsumer(topics: Seq[String]): IO[Unit] = IO {
        underlyingConsumer.subscribe(topics.toList.asJava)
      }

      private def poll: IO[ConsumerRecords[K, V]] = IO {
        underlyingConsumer.poll(Long.MaxValue)
      }

      private def pollStream: Stream[IO, V] =
        Stream.repeatEval(poll)
          .filter(_.count > 0)
          .flatMap { consumerRecords =>
            Stream.emits {
              val records = consumerRecords.iterator.asScala.toSeq
              records.map(_.value)
            }
          }

    }

}



