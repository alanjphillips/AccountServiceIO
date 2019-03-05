package com.alaphi.accountservice.txlog

import cats.effect.{ExitCode, IO, IOApp}
import cats.implicits._
import com.alaphi.accountservice.kafka.client.fs2.KafkaProperties.default
import com.alaphi.accountservice.kafka.client.fs2.KafkaConsumer.mkKafkaConsumer
import com.alaphi.accountservice.model.Accounts.Payload
import com.alaphi.accountservice.model.JsonCodec.decodePayload
import com.alaphi.accountservice.txlog.db.AccountTxDatabase
import com.alaphi.accountservice.txlog.http.TransactionLogApi
import com.alaphi.accountservice.txlog.program.TransactionLogProgram
import com.alaphi.accountservice.txlog.repository.TransactionLogInMemoryRepository
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.implicits._

object AccountTransactionLog extends IOApp {

  val topics = Seq("account", "deposit", "transfer")

  override def run(args: List[String]): IO[ExitCode] =
    for {
      txLogDB <- AccountTxDatabase.createDB
      txLogRepository = new TransactionLogInMemoryRepository(txLogDB)
      txLogProgram = new TransactionLogProgram(txLogRepository)
      txLogApi = new TransactionLogApi(txLogProgram)
      accountTransactionConsumer = mkKafkaConsumer[String, Payload](default)
      consumer = accountTransactionConsumer
        .subscribe(topics)
        .evalMap(txLogDB.append)
      server = BlazeServerBuilder[IO]
        .bindHttp(8081, "0.0.0.0")
        .withHttpApp(txLogApi.routes.orNotFound)
        .serve
      exitcode <- consumer
        .concurrently(server)
        .compile
        .drain
        .as(ExitCode.Success)
    } yield exitcode

}
