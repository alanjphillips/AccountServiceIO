package com.alaphi.accountservice.traffic.http

import cats.effect.IO
import com.alaphi.accountservice.traffic.model.Traffic._
import io.circe.generic.auto._
import org.http4s.circe.{jsonEncoderOf, jsonOf}

object TrafficJsonCodec {
  implicit val decoderTrafficCommand = jsonOf[IO, AccountTrafficCommand]

  implicit val encoderTrafficResult = jsonEncoderOf[IO, AccountTrafficResult]
}
