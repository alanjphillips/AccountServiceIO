package com.alaphi.accountservice.program

import cats.effect.IO
import com.alaphi.accountservice.model.Account._
import com.alaphi.accountservice.repository.AccountRepository

class AccountProgram(accountRepository: AccountRepository) extends AccountAlgebra {

  def create(accountCreation: AccountCreation): IO[Account] =
    accountRepository.create(accountCreation)

  def read(accountNumber: String): IO[Either[AccountError, Account]] =
    accountRepository.read(accountNumber)

  def readAll: IO[Seq[Account]] =
    accountRepository.readAll

  def deposit(accountNumber: String, deposit: Deposit): IO[Either[AccountError, DepositSuccess]] =
    accountRepository.deposit(accountNumber, deposit.depositAmount)

  def transfer(accountNumber: String, transfer: MoneyTransfer): IO[Either[AccountError, TransferSuccess]] =
    accountRepository.transfer(accountNumber, transfer.destAccNum, transfer.transferAmount)

}
