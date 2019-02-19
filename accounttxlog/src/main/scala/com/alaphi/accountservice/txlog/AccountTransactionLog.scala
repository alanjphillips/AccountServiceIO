package com.alaphi.accountservice.txlog

import cats.effect.{ExitCode, IO, IOApp}
import cats.implicits._
import com.alaphi.accountservice.kafka.client.fs2.KafkaProperties.default
import com.alaphi.accountservice.kafka.client.fs2.KafkaConsumer.mkKafkaConsumer
import com.alaphi.accountservice.model.Accounts.Payload
import com.alaphi.accountservice.model.JsonCodec.decodePayload
import com.alaphi.accountservice.txlog.db.AccountTxDatabase

object AccountTransactionLog extends IOApp {

  val topics = Seq("account", "deposit", "transfer")

  override def run(args: List[String]): IO[ExitCode] =
    for {
      txLogDB <- AccountTxDatabase.createDB
      accountTransactionConsumer = mkKafkaConsumer[String, Payload](default)
      txConsumer = accountTransactionConsumer
        .subscribe(topics)
        .map(txLogDB.append)
        .compile
        .drain
      exitcode <- txConsumer.as(ExitCode.Success)
    } yield exitcode

}
