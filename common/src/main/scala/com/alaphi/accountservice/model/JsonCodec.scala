package com.alaphi.accountservice.model

import cats.syntax.functor._
import com.alaphi.accountservice.model.Accounts._
import io.circe.generic.auto._
import io.circe.{Decoder, Encoder}
import io.circe.syntax._

object JsonCodec {

  implicit val encodePayload: Encoder[Payload] = Encoder.instance {
    case accountNotFound @ AccountNotFound(_, _) => accountNotFound.asJson
    case account @ Account(_, _, _) => account.asJson
    case deposit @ DepositSuccess(_, _) => deposit.asJson
    case transfer @ TransferSuccess(_, _, _) => transfer.asJson
  }

  implicit val decodePayload: Decoder[Payload] =
    List[Decoder[Payload]](
      Decoder[Account].widen,
      Decoder[DepositSuccess].widen,
      Decoder[TransferSuccess].widen
    ).reduceLeft(_ or _)
}
