package com.alaphi.accountservice.txlog.http

import cats.effect.IO
import com.alaphi.accountservice.model.Accounts._
import org.http4s.circe.jsonEncoderOf

object TxLogJsonCodec {
  import com.alaphi.accountservice.model.JsonCodec.encodePayload

  implicit val encoderPayloads = jsonEncoderOf[IO, Seq[Payload]]
  implicit val encoderPayload = jsonEncoderOf[IO, Payload]
  implicit val encoderCount = jsonEncoderOf[IO, Int]
}
