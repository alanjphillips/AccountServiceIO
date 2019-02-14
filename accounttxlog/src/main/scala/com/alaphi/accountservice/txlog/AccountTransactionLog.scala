package com.alaphi.accountservice.txlog

import cats.effect.{ExitCode, IO, IOApp}

object AccountTransactionLog extends IOApp {

  override def run(args: List[String]): IO[ExitCode] = {
    IO {
      ExitCode.Success
    }
  }
}
