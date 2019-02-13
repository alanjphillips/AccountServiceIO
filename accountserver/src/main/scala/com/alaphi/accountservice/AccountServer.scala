package com.alaphi.accountservice

import cats.effect._
import cats.syntax.all._
import com.alaphi.accountservice.db.AccountInMemoryDatabase
import com.alaphi.accountservice.http.AccountApi
import com.alaphi.accountservice.kafka.client.fs2.KafkaPublisher.mkKafkaPublisher
import com.alaphi.accountservice.kafka.client.fs2.KafkaProperties.default
import com.alaphi.accountservice.model.Account.Payload
import com.alaphi.accountservice.model.JsonCodec.encodePayload
import com.alaphi.accountservice.program.AccountProgram
import com.alaphi.accountservice.repository.AccountInMemoryRepository
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.implicits._

object AccountServer extends IOApp {

  override def run(args: List[String]): IO[ExitCode] = {
    for {
      accountDB <- AccountInMemoryDatabase.createDB
      accountRepository = new AccountInMemoryRepository(accountDB)
      kafkaPublisher = mkKafkaPublisher[String, Payload](default)
      accountProgram = new AccountProgram(accountRepository, kafkaPublisher)
      accountApi = new AccountApi(accountProgram)
      server = BlazeServerBuilder[IO]
        .bindHttp(8080, "0.0.0.0")
        .withHttpApp(accountApi.routes.orNotFound)
        .serve
        .compile
        .drain
      exitCode <- server.as(ExitCode.Success)
    } yield exitCode
  }

}






