package com.alaphi.accountservice.txlog.repository

import cats.effect.IO
import com.alaphi.accountservice.model.Accounts.Payload

trait TransactionLogRepository {

  def read(offset: Int): IO[Payload]

  def readAll: IO[Seq[Payload]]

  def size: IO[Int]

}
