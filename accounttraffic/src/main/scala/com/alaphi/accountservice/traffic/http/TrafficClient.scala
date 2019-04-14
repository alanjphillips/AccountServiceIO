package com.alaphi.accountservice.traffic.http

import com.alaphi.accountservice.model.Accounts.Payload
import com.alaphi.accountservice.traffic.http.TrafficJson._
import cats.effect.{ConcurrentEffect, IO, Resource}
import org.http4s._
import org.http4s.client.Client
import org.http4s.client.dsl.io._
import org.http4s.dsl.Http4sDsl

import scala.concurrent.ExecutionContext

class TrafficClient(client: Resource[IO, Client[IO]])(implicit ec: ExecutionContext, ctx: ConcurrentEffect[IO]) extends Http4sDsl[IO] {

  def post(payload: Payload, uri: Uri): IO[Payload] = client.use(_.expect[Payload](POST(payload, uri)))

}
