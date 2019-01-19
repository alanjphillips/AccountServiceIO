package com.alaphi.accountservice.http

import cats.effect.IO
import com.alaphi.accountservice.model.Account.{Account, AccountCreation, AccountNotFound}
import org.http4s.circe.{jsonOf, jsonEncoderOf}
import io.circe.generic.auto._

object JsonCodec {
  implicit val decoderrAccCreate = jsonOf[IO, AccountCreation]
  implicit val encoderAcc = jsonEncoderOf[IO, Account]
}
