package com.alaphi.accountservice.kafka.fs2

import java.util.Properties

import fs2._
import cats.effect._

import scala.collection.JavaConverters._
import org.apache.kafka.clients.consumer.{ConsumerConfig, ConsumerRecords, KafkaConsumer}

class KafkaFs2Consumer(consumer: KafkaConsumer[String, String]) {

  def subscribe(topics: Seq[String]): Stream[IO, String] =
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

  def apply: KafkaFs2Consumer = KafkaFs2Consumer(KafkaProperties.default)

}

object KafkaProperties {
  val default = {
    val props = new Properties()
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "kafka-1:9092")
    props.put(ConsumerConfig.GROUP_ID_CONFIG, "streamgroup")
    props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true")
    props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000")
    props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "300000")
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer")
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer")
    props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")
    props
  }
}



