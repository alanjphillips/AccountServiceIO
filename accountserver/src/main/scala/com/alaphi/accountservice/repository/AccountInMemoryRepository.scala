package com.alaphi.accountservice.repository

import cats.effect.{IO, Sync}
import com.alaphi.accountservice.model.Account.{Account, AccountError, AccountNotFound}

class AccountInMemoryRepository extends AccountRepository {

  def read(accountNumber: String): IO[Either[AccountError, Account]] = accountNumber match {
    case "0000" =>
      IO(Left(AccountNotFound(accountNumber, s"$accountNumber not exists")))
    case _ =>
      IO(Right(Account(accountNumber, "Aaa Baaa", 100)))
  }

}
