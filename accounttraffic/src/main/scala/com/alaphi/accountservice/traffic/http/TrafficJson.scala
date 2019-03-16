package com.alaphi.accountservice.traffic.http

import cats.effect.IO
import com.alaphi.accountservice.model.Accounts.AccountCreation
import com.alaphi.accountservice.traffic.model.Traffic._
import io.circe.generic.auto._
import org.http4s.circe.{jsonEncoderOf, jsonOf}

object TrafficJson {
  implicit val decoderTrafficCommand = jsonOf[IO, AccountTrafficCommand]

  implicit val encoderTrafficCommand = jsonEncoderOf[IO, AccountTrafficCommand]
  implicit val encoderTrafficResult = jsonEncoderOf[IO, AccountTrafficResult]

  implicit val encoderAccCreate = jsonEncoderOf[IO, AccountCreation]
}
