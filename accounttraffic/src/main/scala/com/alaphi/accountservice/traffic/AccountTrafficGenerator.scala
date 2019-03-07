package com.alaphi.accountservice.traffic

import cats.syntax.all._
import cats.effect.{ExitCode, IO, IOApp}
import org.http4s.server.blaze.BlazeServerBuilder

object AccountTrafficGenerator extends IOApp {

  override def run(args: List[String]): IO[ExitCode] = {
    BlazeServerBuilder[IO]
      .bindHttp(8082, "0.0.0.0")
      .withHttpApp(???)
      .serve
      .compile
      .drain
      .as(ExitCode.Success)
  }
}
