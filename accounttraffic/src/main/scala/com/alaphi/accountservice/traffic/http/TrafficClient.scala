package com.alaphi.accountservice.traffic.http

import cats.effect.{ConcurrentEffect, IO, Resource}
import com.alaphi.accountservice.model.Accounts.Payload
import com.alaphi.accountservice.traffic.http.TrafficJson._
import org.http4s._
import org.http4s.client.Client
import org.http4s.client.blaze.BlazeClientBuilder
import org.http4s.client.dsl.Http4sClientDsl

import scala.concurrent.ExecutionContext

class TrafficClient(implicit ec: ExecutionContext, ctx: ConcurrentEffect[IO]) extends Http4sClientDsl[IO] {

  val client: Resource[IO, Client[IO]] = BlazeClientBuilder[IO](ec).resource

  def send(request: Request[IO]): IO[Payload] = client.use(_.expect[Payload](request))

}
