package com.alaphi.accountservice.traffic.program

import java.time.Instant

import cats.effect.IO
import com.alaphi.accountservice.model.Accounts.AccountCreation
import com.alaphi.accountservice.traffic.http.TrafficClient
import com.alaphi.accountservice.traffic.model.Traffic.{AccountTrafficCommand, AccountTrafficResult}
import org.http4s.Uri
import cats.implicits._


class TrafficProgram(client: TrafficClient) extends TrafficAlgebra {

  override def runTraffic(accountTrafficCommand: AccountTrafficCommand): IO[AccountTrafficResult] = {
    val startTimeEpochMillis = Instant.now.toEpochMilli

    val sent =
      (1 to accountTrafficCommand.numSeedAccounts).map { accNameSuffix =>
        client.post(
          AccountCreation(accHolderName =  s"acc_name_$accNameSuffix", balance = 1000),
          Uri.uri("http://accountserver-service:8080/accounts"))
      }.toList.sequence

    val endTimeEpochMillis = Instant.now().toEpochMilli

    sent.map(_ => AccountTrafficResult(endTimeEpochMillis - startTimeEpochMillis))
  }

}
