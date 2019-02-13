package com.alaphi.accountservice.repository

import cats.effect.IO
import com.alaphi.accountservice.model.Accounts._

trait AccountRepository {
  def create(accountCreation: AccountCreation): IO[Account]
  def read(accountNumber: String): IO[Either[AccountError, Account]]
  def readAll: IO[Seq[Account]]
  def deposit(accountNumber: String, amount: Int): IO[Either[AccountError, DepositSuccess]]
  def transfer(srcAccNum: String, destAccNum: String, amount: Int): IO[Either[AccountError, TransferSuccess]]
}
