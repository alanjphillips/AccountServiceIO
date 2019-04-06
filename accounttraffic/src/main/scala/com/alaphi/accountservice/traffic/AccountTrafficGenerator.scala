package com.alaphi.accountservice.traffic

import cats.effect.{ExitCode, IO, IOApp}
import cats.implicits._
import com.alaphi.accountservice.traffic.http.{TrafficApi, TrafficClient}
import com.alaphi.accountservice.traffic.program.TrafficProgram
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.implicits._
import scala.concurrent.ExecutionContext.Implicits.global

object AccountTrafficGenerator extends IOApp {

  override def run(args: List[String]): IO[ExitCode] = {
    val trafficProgram = new TrafficProgram
    val trafficApi = new TrafficApi(trafficProgram)
    val trafficClient = new TrafficClient

    BlazeServerBuilder[IO]
      .bindHttp(8082, "0.0.0.0")
      .withHttpApp(trafficApi.routes.orNotFound)
      .serve
      .compile
      .drain
      .as(ExitCode.Success)
  }
}
