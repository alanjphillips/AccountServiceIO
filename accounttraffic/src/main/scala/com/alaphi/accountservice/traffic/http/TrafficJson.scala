package com.alaphi.accountservice.traffic.http

import com.alaphi.accountservice.traffic.model.Traffic.{AccountTrafficCommand, AccountTrafficResult}
import com.alaphi.accountservice.model.Accounts._
import cats.effect.IO
import org.http4s.circe.{jsonEncoderOf, jsonOf}
import cats.syntax.functor._
import io.circe.generic.auto._
import io.circe.syntax._
import io.circe.{Decoder, Encoder}


object TrafficJson {
  implicit val decodePayload: Decoder[Payload] =
    List[Decoder[Payload]](
      Decoder[Account].widen
    ).reduceLeft(_ or _)

  implicit val decoderEntityPayload = jsonOf[IO, Payload]
  implicit val decoderTrafficCommand = jsonOf[IO, AccountTrafficCommand]
  implicit val decoderAccount = jsonOf[IO, Account]

  implicit val encodePayload: Encoder[Payload] = Encoder.instance {
    case accountCreate @ AccountCreation(_, _) => accountCreate.asJson
  }

  implicit val encoderEntityPayload = jsonEncoderOf[IO, Payload]
  implicit val encoderTrafficCommand = jsonEncoderOf[IO, AccountTrafficCommand]
  implicit val encoderTrafficResult = jsonEncoderOf[IO, AccountTrafficResult]
  implicit val encoderAccCreate = jsonEncoderOf[IO, AccountCreation]

}
