package com.alaphi.accountservice.model

import com.alaphi.accountservice.model.Accounts._
import io.circe.generic.auto._
import io.circe.{Encoder, Json}
import io.circe.syntax._

object JsonCodec {

  implicit val encodePayload: Encoder[Payload] = Encoder.instance {
    case accountNotFound @ AccountNotFound(_, _) => accountNotFound.asJson
    case account @ Account(_, _, _) => account.asJson
    case deposit @ DepositSuccess(_, _) => deposit.asJson
    case transfer @ TransferSuccess(_, _, _) => transfer.asJson
  }

}
