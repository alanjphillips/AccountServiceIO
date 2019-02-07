package com.alaphi.accountservice.resilience

import cats.effect._
import scala.concurrent.duration._

object Retry {

  def retryWithBackoff[A](ioAction: IO[A], initialDelay: FiniteDuration, maxRetries: Int)
                         (implicit timer: Timer[IO]): IO[A] = {
    ioAction.handleErrorWith { error =>
      if (maxRetries > 0)
        IO.sleep(initialDelay).flatMap(_ => retryWithBackoff(ioAction, initialDelay * 2, maxRetries - 1))
      else
        IO.raiseError(error)
    }
  }

}
