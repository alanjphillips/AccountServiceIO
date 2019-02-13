package com.alaphi.accountservice.repository

import cats.effect.IO
import com.alaphi.accountservice.db.AccountInMemoryDatabase
import com.alaphi.accountservice.model.Accounts._

class AccountInMemoryRepository(db: AccountInMemoryDatabase) extends AccountRepository {

  def create(accountCreation: AccountCreation): IO[Account] =
    db.create(accountCreation)

  def read(accountNumber: String): IO[Either[AccountError, Account]] =
    db.read(accountNumber).value

  def readAll: IO[Seq[Account]] = db.readAll

  def deposit(accountNumber: String, amount: Int): IO[Either[AccountError, DepositSuccess]] =
    db.deposit(accountNumber, amount).value

  def transfer(srcAccNum: String, destAccNum: String, amount: Int): IO[Either[AccountError, TransferSuccess]] =
    db.transfer(srcAccNum, destAccNum, amount).value

}
