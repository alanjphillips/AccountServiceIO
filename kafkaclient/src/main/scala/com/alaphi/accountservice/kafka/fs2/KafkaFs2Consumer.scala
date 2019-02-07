package com.alaphi.accountservice.kafka.fs2

import java.util.Properties

import fs2._
import cats.effect._
import scala.collection.JavaConverters._
import org.apache.kafka.clients.consumer.{ConsumerRecords, KafkaConsumer}

class KafkaFs2Consumer(consumer: KafkaConsumer[String, String]) {

  def subscribe(topics: Seq[String])(implicit F: ConcurrentEffect[IO], cs: ContextShift[IO]): Stream[IO, String] =
    Stream.eval_(subscribeViaKafkaConsumer(topics)) ++ pollStream

  private def subscribeViaKafkaConsumer(topics: Seq[String]): IO[Unit] = IO {
    consumer.subscribe(topics.toList.asJava)
  }

  private def poll: IO[ConsumerRecords[String, String]] = IO {
    consumer.poll(Long.MaxValue)
  }

  private def pollStream: Stream[IO, String] =
    Stream.repeatEval(poll)
      .filter(_.count > 0)
      .flatMap { consumerRecords =>
        Stream.emits {
          val records = consumerRecords.iterator.asScala.toSeq
          records.map(_.value)
        }
      }
}

object KafkaFs2Consumer {
  def apply(props: Properties): KafkaFs2Consumer =
    new KafkaFs2Consumer(
      new KafkaConsumer[String, String](props)
    )

}



