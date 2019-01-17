package com.alaphi.accountservice.http

import cats.effect.IO
import com.alaphi.accountservice.model.Account.{Account, AccountNotFound}
import org.http4s.circe.jsonEncoderOf
import io.circe.generic.auto._

object JsonCodec {
  implicit val encoderAcc = jsonEncoderOf[IO, Account]
}
