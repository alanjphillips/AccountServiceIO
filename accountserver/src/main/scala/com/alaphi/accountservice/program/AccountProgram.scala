package com.alaphi.accountservice.program

import cats.effect.{IO, Timer}
import com.alaphi.accountservice.kafka.client.fs2.KafkaPublisher
import com.alaphi.accountservice.model.Account._
import com.alaphi.accountservice.repository.AccountRepository
import com.alaphi.accountservice.resilience.Retry.retryWithBackoff

import scala.concurrent.duration._

class AccountProgram(accountRepository: AccountRepository,
                     kafkaPublisher: KafkaPublisher[String, Payload])
                    (implicit timer: Timer[IO]) extends AccountAlgebra {

  def create(accountCreation: AccountCreation): IO[Account] = for {
    account <- retryWithBackoff(
      accountRepository.create(accountCreation),
      initialDelay = 1 second,
      maxRetries = 3)
    _ <- kafkaPublisher.send("accountcreated", account.accNumber, accountCreation)
  } yield account

  def read(accountNumber: String): IO[Either[AccountError, Account]] =
    accountRepository.read(accountNumber)

  def readAll: IO[Seq[Account]] =
    accountRepository.readAll

  def deposit(accountNumber: String, deposit: Deposit): IO[Either[AccountError, DepositSuccess]] =
    accountRepository.deposit(accountNumber, deposit.depositAmount)

  def transfer(accountNumber: String, transfer: MoneyTransfer): IO[Either[AccountError, TransferSuccess]] =
    accountRepository.transfer(accountNumber, transfer.destAccNum, transfer.transferAmount)

}
