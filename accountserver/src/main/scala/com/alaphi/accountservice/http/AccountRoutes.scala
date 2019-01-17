package com.alaphi.accountservice.http

import cats.effect.IO
import com.alaphi.accountservice.program.AccountAlgebra
import com.alaphi.accountservice.http.JsonCodec._
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl

class AccountRoutes(accountAlgebra: AccountAlgebra) extends Http4sDsl[IO] {

  val routes: HttpRoutes[IO] = HttpRoutes.of[IO]  {
    case GET -> Root / "accounts" / number =>
      accountAlgebra
        .read(number)
        .flatMap {
          _.fold(nf => NotFound(nf.description), acc => Ok(acc))
        }
  }

}
