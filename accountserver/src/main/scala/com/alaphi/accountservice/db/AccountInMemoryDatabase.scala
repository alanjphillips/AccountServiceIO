package com.alaphi.accountservice.db

import cats.effect.IO
import cats.effect.concurrent.Ref
import com.alaphi.accountservice.model.Account._

class AccountInMemoryDatabase(storage: Ref[IO, Map[String, Account]]) {

  def create(accountCreation: AccountCreation): IO[Account] = for {
    num <- generateAccountNumber
    account =  Account(num, accountCreation.accHolderName, accountCreation.balance)
    _ <- storage.update(accs => accs.updated(account.accNumber, account))
  } yield account

  def read(accountNumber: String): IO[Either[AccountError, Account]] =
    storage.get.map(accounts =>
      accounts.get(accountNumber)
        .toRight[AccountNotFound](AccountNotFound(accountNumber, s"Account Number doesn't exist: $accountNumber")))

  def readAll: IO[Seq[Account]] =
    storage.get.map(_.values.toSeq)

  def transfer(src: Account, dest: Account, amount: Int): IO[Either[AccountError, TransferSuccess]] =
    if (src.balance >= amount) {
      val updatedStorage = storage.update {
        accounts =>
          accounts
            .updated(src.accNumber, src.copy(balance = src.balance - amount))
            .updated(dest.accNumber, dest.copy(balance = dest.balance + amount))
      }

      updatedStorage.map(_ => Right(TransferSuccess(src.accNumber, dest.accNumber, amount)))
    } else
      IO(
        Left(TransferFailed(src.accNumber, dest.accNumber, amount, s"Not enough funds available in account number: ${src.accNumber}"))
      )

  def generateAccountNumber: IO[String] =
    storage.get.map(accounts => (accounts.size + 1).toString)

}

object AccountInMemoryDatabase {

  def createDB: IO[AccountInMemoryDatabase] =
    Ref.of[IO, Map[String, Account]](Map.empty[String, Account])
      .map(new AccountInMemoryDatabase(_))

}
