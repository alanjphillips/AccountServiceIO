package com.alaphi.accountservice.traffic.http

import cats.effect.IO
import com.alaphi.accountservice.model.Accounts._
import io.circe.generic.auto._
import io.circe.Decoder
import org.http4s.circe.jsonOf


object TrafficAJson {

  implicit def decoderPayload[A <: Payload]: Decoder[A] = implicitly
  implicit def decoderPayloads[A <: Payload] = jsonOf[IO, Seq[A]]

}
