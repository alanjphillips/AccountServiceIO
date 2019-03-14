package com.alaphi.accountservice.traffic.http

import cats.effect.{ConcurrentEffect, IO}
import com.alaphi.accountservice.model.Accounts.AccountCreation
import com.alaphi.accountservice.traffic.model.Traffic.{AccountTrafficCommand, AccountTrafficResult}
import org.http4s.Uri
import org.http4s.client.blaze.BlazeClientBuilder
import org.http4s.dsl.Http4sDsl
import org.http4s.client.dsl.io._
import com.alaphi.accountservice.traffic.http.TrafficJsonCodec.encoderAccCreate

import scala.concurrent.ExecutionContext


object TrafficClient extends Http4sDsl[IO] {

  def runTraffic(accountTrafficCommand: AccountTrafficCommand)(implicit ec: ExecutionContext, ctx: ConcurrentEffect[IO]): IO[AccountTrafficResult] = {
//    val req = POST(AccountCreation("", 0), Uri.uri("http://account-server:8080/accounts"))
//    BlazeClientBuilder[IO](ec).resource.use { client =>
//      client.expect(req)
//    }.map(_ => new AccountTrafficResult(10000))
    IO.pure(new AccountTrafficResult(10000))
  }

}
