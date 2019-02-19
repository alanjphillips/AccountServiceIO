package com.alaphi.accountservice.txlog.db

import cats.effect.IO
import cats.effect.concurrent.Ref
import com.alaphi.accountservice.model.Accounts.Payload

class AccountTxDatabase private(storage: Ref[IO, Seq[Payload]]) {

  def append(tx: Payload): IO[Unit] =
    storage.update(logStore => logStore :+ tx)

  def read(offset: Int): IO[Payload] =
    storage.get.map(logStore => logStore(offset))

}

object AccountTxDatabase {
  def createDB: IO[AccountTxDatabase] =
    Ref.of[IO, Seq[Payload]](Seq.empty[Payload])
      .map(new AccountTxDatabase(_))
}
