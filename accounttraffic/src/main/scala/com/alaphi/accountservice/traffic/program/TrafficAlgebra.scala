package com.alaphi.accountservice.traffic.program

import cats.effect.IO
import com.alaphi.accountservice.traffic.model.Traffic.{AccountTrafficCommand, AccountTrafficResult}

trait TrafficAlgebra {
  def runTraffic(accountTrafficCommand: AccountTrafficCommand): IO[AccountTrafficResult]
}
