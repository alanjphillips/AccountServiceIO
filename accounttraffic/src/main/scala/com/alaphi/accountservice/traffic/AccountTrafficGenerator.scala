package com.alaphi.accountservice.traffic

import cats.effect.{ExitCode, IO, IOApp}
import cats.implicits._
import com.alaphi.accountservice.traffic.http.TrafficApi
import com.alaphi.accountservice.traffic.program.TrafficProgram
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.implicits._

object AccountTrafficGenerator extends IOApp {

  override def run(args: List[String]): IO[ExitCode] = {
    val trafficProgram = new TrafficProgram
    val trafficApi = new TrafficApi(trafficProgram)

    BlazeServerBuilder[IO]
      .bindHttp(8082, "0.0.0.0")
      .withHttpApp(trafficApi.routes.orNotFound)
      .serve
      .compile
      .drain
      .as(ExitCode.Success)
  }
}
