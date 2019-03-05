package com.alaphi.accountservice.txlog.http

import cats.effect.IO
import com.alaphi.accountservice.txlog.program.TransactionLogAlgebra
import com.alaphi.accountservice.txlog.http.TxLogJsonCodec.encoderCount
import com.alaphi.accountservice.txlog.http.TxLogJsonCodec.encoderPayloads
import com.alaphi.accountservice.txlog.http.TxLogJsonCodec.encoderPayload
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl

import scala.util.Try

class TransactionLogApi(txLogAlgebra: TransactionLogAlgebra) extends Http4sDsl[IO] {

  val routes: HttpRoutes[IO] = HttpRoutes.of[IO]  {
    case GET -> Root / "transactions" / index =>
      txLogAlgebra
        .read(Try(index.toInt).getOrElse(0))
        .flatMap (Ok(_))
        .handleErrorWith {
          case err => BadRequest(err.getMessage)
        }

    case GET -> Root / "transactions" =>
      txLogAlgebra
        .readAll
        .flatMap(Ok(_))
        .handleErrorWith {
          case err => BadRequest(err.getMessage)
        }

    case GET -> Root / "transactions" / "count" / "logged" =>
      txLogAlgebra
        .size
        .flatMap(Ok(_))
        .handleErrorWith {
          case err => BadRequest(err.getMessage)
        }
  }

}
