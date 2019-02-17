package com.alaphi.accountservice.txlog

import cats.effect.IO
import fs2.Stream
import com.alaphi.accountservice.kafka.client.fs2.KafkaConsumer
import com.alaphi.accountservice.model.Accounts.Payload

trait AccountTransactionConsumer {
  def addConsumer(kafkaConsumer: KafkaConsumer[String, Payload], topics: Seq[String]): Stream[IO, Payload]
}

object AccountTransactionConsumer {

  def mkAccountTransactionConsumer: AccountTransactionConsumer =
    new AccountTransactionConsumer {
      override def addConsumer(kafkaConsumer: KafkaConsumer[String, Payload], topics: Seq[String]): Stream[IO, Payload] =
        kafkaConsumer.subscribe(topics)
    }

}


