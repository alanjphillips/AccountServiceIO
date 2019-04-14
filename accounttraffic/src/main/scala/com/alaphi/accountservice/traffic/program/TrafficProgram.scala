package com.alaphi.accountservice.traffic.program

import java.time.Instant

import cats.effect.IO
import com.alaphi.accountservice.model.Accounts.AccountCreation
import com.alaphi.accountservice.traffic.http.TrafficClient
import com.alaphi.accountservice.traffic.model.Traffic.{AccountTrafficCommand, AccountTrafficResult}
import org.http4s.Uri
import cats.implicits._


class TrafficProgram(client: TrafficClient) extends TrafficAlgebra {

  override def createDepositTransfer(accountTrafficCommand: AccountTrafficCommand): IO[AccountTrafficResult] = {
    val startTimeEpochMillis = Instant.now.toEpochMilli

    val sent =
      (1 to accountTrafficCommand.numSeedAccounts).map { accNameSuffix =>
        client.post(AccountCreation(s"acc_name_$accNameSuffix"),
          Uri.uri("http://account-server:8080/accounts"))
      }.toList.sequence

    val endTimeEpochMillis = Instant.now().toEpochMilli
    sent.map(_ => AccountTrafficResult(endTimeEpochMillis - startTimeEpochMillis))
  }

}
