package com.alaphi.accountservice.txlog

import cats.effect.{ExitCode, IO, IOApp}
import cats.implicits._
import com.alaphi.accountservice.txlog.AccountTransactionConsumer.mkAccountTransactionConsumer
import com.alaphi.accountservice.kafka.client.fs2.KafkaProperties.default
import com.alaphi.accountservice.kafka.client.fs2.KafkaConsumer.mkKafkaConsumer
import com.alaphi.accountservice.model.Accounts.Payload
import com.alaphi.accountservice.model.JsonCodec.decodePayload

object AccountTransactionLog extends IOApp {

  val topics = Seq("account", "deposit", "transfer")

  override def run(args: List[String]): IO[ExitCode] = {
    val kafkaConsumer = mkKafkaConsumer[String, Payload](default)

    mkAccountTransactionConsumer
      .addConsumer(kafkaConsumer, topics)
      .through(_.evalMap { m =>
        IO { println(s"MESSAGE: $m"); m }
      })
      .compile
      .drain
      .as(ExitCode.Success)
  }

}
