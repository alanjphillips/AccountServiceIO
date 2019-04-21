package com.alaphi.accountservice.traffic.program

import java.time.Instant

import cats.effect.IO
import com.alaphi.accountservice.model.Accounts.{Account, AccountCreation, Payload}
import com.alaphi.accountservice.traffic.http.TrafficClient
import com.alaphi.accountservice.traffic.model.Traffic.{AccountTrafficCommand, AccountTrafficResult}
import org.http4s.Uri
import cats.implicits._


class TrafficProgram(client: TrafficClient) extends TrafficAlgebra {

  override def runTraffic(accountTrafficCommand: AccountTrafficCommand): IO[AccountTrafficResult] =
    for {
      startTimeEpochMillis <- IO.pure(Instant.now.toEpochMilli)
      _ <- createSeedAccounts(accountTrafficCommand.numSeedAccounts)
      _ <- getAllAccounts
      endTimeEpochMillis <- IO.pure(Instant.now().toEpochMilli)
    } yield
      AccountTrafficResult(endTimeEpochMillis - startTimeEpochMillis)

  private def createSeedAccounts(numSeedAccounts: Int): IO[List[Payload]] = {
    (1 to numSeedAccounts).map { accNameSuffix =>
      client.post(
        AccountCreation(accHolderName =  s"acc_name_$accNameSuffix", balance = 1000),
        Uri.uri("http://accountserver-service:8080/accounts"))
    }.toList.sequence
  }

  private def getAllAccounts: IO[Seq[Account]] =
    client.getMany[Account](Uri.uri("http://accountserver-service:8080/accounts"))

}
