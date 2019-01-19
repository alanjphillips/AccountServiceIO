package com.alaphi.accountservice

import cats.effect._
import cats.syntax.all._
import com.alaphi.accountservice.http.AccountApi
import com.alaphi.accountservice.program.AccountProgram
import com.alaphi.accountservice.repository.AccountInMemoryRepository
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.implicits._

object AccountServer extends IOApp {

  override def run(args: List[String]): IO[ExitCode] = {
    val accountRepository = new AccountInMemoryRepository
    val accountProgram = new AccountProgram(accountRepository)
    val accountApi = new AccountApi(accountProgram)

    val server = BlazeServerBuilder[IO]
      .bindHttp(8080, "0.0.0.0")
      .withHttpApp(accountApi.routes.orNotFound)
      .serve
      .compile
      .drain

    server.as(ExitCode.Success)
  }

}






