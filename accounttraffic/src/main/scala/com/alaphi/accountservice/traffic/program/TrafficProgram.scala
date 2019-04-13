package com.alaphi.accountservice.traffic.program
import cats.effect.IO
import com.alaphi.accountservice.traffic.http.TrafficClient
import com.alaphi.accountservice.traffic.model.Traffic.{AccountTrafficCommand, AccountTrafficResult}

class TrafficProgram(client: TrafficClient) extends TrafficAlgebra {

  override def createDepositTransfer(accountTrafficCommand: AccountTrafficCommand): IO[AccountTrafficResult] =
    IO.pure(AccountTrafficResult(300000L))

}
