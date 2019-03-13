package com.alaphi.accountservice.traffic.http

import cats.effect.IO
import com.alaphi.accountservice.traffic.model.Traffic._
import com.alaphi.accountservice.traffic.http.TrafficJsonCodec._
import com.alaphi.accountservice.traffic.program.TrafficAlgebra
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl

class TrafficApi(trafficAlgebra: TrafficAlgebra) extends Http4sDsl[IO] {

  val routes: HttpRoutes[IO] = HttpRoutes.of[IO]  {
    case req @ POST -> Root / "accounttraffic" =>
      req.decode[AccountTrafficCommand] { trafficCommand =>
        trafficAlgebra
          .createDepositTransfer(trafficCommand)
          .flatMap(Created(_))
          .handleErrorWith {
            case err => BadRequest(err.getMessage)
          }
      }
  }

}
