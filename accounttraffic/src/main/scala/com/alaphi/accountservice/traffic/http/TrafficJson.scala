package com.alaphi.accountservice.traffic.http

import com.alaphi.accountservice.model.Accounts._
import cats.effect.IO
import org.http4s.circe.{jsonEncoderOf, jsonOf}
import cats.syntax.functor._
import com.alaphi.accountservice.traffic.model.Traffic.{AccountTrafficCommand, AccountTrafficResult}
import io.circe.generic.auto._
import io.circe.Decoder


object TrafficJson {
  implicit val decoderTrafficCommand = jsonOf[IO, AccountTrafficCommand]

  implicit val encoderTrafficCommand = jsonEncoderOf[IO, AccountTrafficCommand]
  implicit val encoderTrafficResult = jsonEncoderOf[IO, AccountTrafficResult]

  implicit val decodePayload: Decoder[Payload] =
    List[Decoder[Payload]](
      Decoder[Account].widen
    ).reduceLeft(_ or _)

  implicit val decoderPayload = jsonOf[IO, Payload]
}
