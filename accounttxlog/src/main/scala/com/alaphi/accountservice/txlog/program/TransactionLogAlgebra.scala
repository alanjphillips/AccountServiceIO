package com.alaphi.accountservice.txlog.program

import cats.effect.IO
import com.alaphi.accountservice.model.Accounts.Payload

trait TransactionLogAlgebra {

  def read(offset: Int): IO[Payload]

  def readAll: IO[Seq[Payload]]

  def size: IO[Int]

}
