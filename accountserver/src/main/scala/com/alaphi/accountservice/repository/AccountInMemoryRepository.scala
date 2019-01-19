package com.alaphi.accountservice.repository

import cats.effect.{IO, Sync}
import com.alaphi.accountservice.model.Account.{Account, AccountCreation, AccountError, AccountNotFound}

class AccountInMemoryRepository extends AccountRepository {

  def create(accountCreation: AccountCreation): IO[Account] = {
    IO(Account("10001", accountCreation.accHolderName, accountCreation.balance))
  }

  def read(accountNumber: String): IO[Either[AccountError, Account]] = accountNumber match {
    case "0000" =>
      IO(Left(AccountNotFound(accountNumber, s"$accountNumber not exists")))
    case _ =>
      IO(Right(Account(accountNumber, "Aaa Baaa", 100)))
  }

  def readAll: IO[Seq[Account]] =
    IO {
      Seq(
        Account("10001", "Aaa Bbbb", 100),
        Account("10002", "Ccc Dddd", 200),
        Account("10003", "Eee Ffff", 300)
      )
    }

}
