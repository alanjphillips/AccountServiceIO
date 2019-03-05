package com.alaphi.accountservice.txlog.repository

import cats.effect.IO
import com.alaphi.accountservice.model.Accounts.Payload
import com.alaphi.accountservice.txlog.db.AccountTxDatabase

class TransactionLogInMemoryRepository(db: AccountTxDatabase) extends TransactionLogRepository {

  def read(offset: Int): IO[Payload] = db.read(offset)

  def readAll: IO[Seq[Payload]] = db.readAll

  def size: IO[Int] = db.size

}
