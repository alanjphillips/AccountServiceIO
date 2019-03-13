package com.alaphi.accountservice.traffic.program
import cats.effect.IO
import com.alaphi.accountservice.traffic.model.Traffic
import com.alaphi.accountservice.traffic.model.Traffic.AccountTrafficResult

class TrafficProgram extends TrafficAlgebra {
  override def createDepositTransfer(accountTrafficCommand: Traffic.AccountTrafficCommand): IO[Traffic.AccountTrafficResult] =
    IO.pure(AccountTrafficResult(300000L))
}
