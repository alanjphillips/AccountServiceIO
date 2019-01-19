package com.alaphi.accountservice.http

import cats.effect.IO
import com.alaphi.accountservice.program.AccountAlgebra
import com.alaphi.accountservice.http.JsonCodec._
import com.alaphi.accountservice.model.Account.AccountCreation
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl

class AccountApi(accountAlgebra: AccountAlgebra) extends Http4sDsl[IO] {

  val routes: HttpRoutes[IO] = HttpRoutes.of[IO]  {
    case GET -> Root / "accounts" / number =>
      accountAlgebra
        .read(number)
        .flatMap {
          _.fold(nf => NotFound(nf.description), acc => Ok(acc))
        }

    case GET -> Root / "accounts" =>
      accountAlgebra
        .readAll
        .flatMap(Ok(_))

    case req @ POST -> Root / "accounts" =>
      req.decode[AccountCreation] { accCreate =>
        accountAlgebra
          .create(accCreate)
          .flatMap(Created(_))
          .handleErrorWith {
            case err => BadRequest(err.getMessage)
          }
      }

  }

}
