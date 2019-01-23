package com.alaphi.accountservice.program

import cats.effect.IO
import com.alaphi.accountservice.model.Account._

trait AccountAlgebra {
  def create(accountCreation: AccountCreation): IO[Account]
  def read(accountNumber: String): IO[Either[AccountError, Account]]
  def readAll: IO[Seq[Account]]
  def deposit(accountNumber: String, deposit: Deposit): IO[Either[AccountError, DepositSuccess]]
  def transfer(accountNumber: String, transfer: MoneyTransfer): IO[Either[AccountError, TransferSuccess]]
}
