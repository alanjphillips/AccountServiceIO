package com.alaphi.accountservice.db

import cats.data.EitherT
import cats.effect.{Concurrent, IO, Timer}
import cats.effect.concurrent.{Ref, Semaphore}
import com.alaphi.accountservice.model.Account._

import scala.concurrent.ExecutionContext

class AccountInMemoryDatabase private(storage: Ref[IO, Map[String, AccountAccess]]) {

  implicit val ctx = IO.contextShift(ExecutionContext.global)
  implicit val timer = IO.timer(ExecutionContext.global)

  def create(accountCreation: AccountCreation): IO[Account] = for {
    accountNumber <- generateAccountNumber
    accountAccess <- AccountAccess.create(Account(accountNumber, accountCreation.accHolderName, accountCreation.balance))
    account = accountAccess.account
    _ <- storage.update(_.updated(account.accNumber, accountAccess))
  } yield account

  def read(accountNumber: String): EitherT[IO, AccountError, Account] = for {
    accAccess <- getAccountAccess(accountNumber)
    _ <- accAccess.acquireAccount
    acc <- accAccess.account
    _ <- accAccess.releaseAccount
  } yield acc

  def readAll: IO[Seq[Account]] =
    storage.get.map(_.values.map(_.account).toSeq)

  def deposit(accountNumber: String, amount: Int): EitherT[IO, AccountError, DepositSuccess] = for {
    accAccess <- getAccountAccess(accountNumber)
    _ <- accAccess.acquireAccount
    depositResult <-
      storage.update(accAccMap =>
        accAccMap.updated(accAccess.account.accNumber, accAccess.copy(account = accAccess.account.copy(balance = accAccess.account.balance + amount)))
      ).map(_ => Right(DepositSuccess(accAccess.account, amount)))
    _ <- accAccess.releaseAccount
  } yield depositResult

  def transfer(srcAccNum: String, destAccNum: String, amount: Int): EitherT[IO, AccountError, TransferSuccess] = for {
    accAccessSrc <- getAccountAccess(srcAccNum)
    accAccessDest <- getAccountAccess(destAccNum)
    _ <- accAccessSrc.acquireAccount
    _ <- accAccessDest.acquireAccount
    transferResult <- adjust(accAccessSrc, accAccessDest, amount)
    _ <- accAccessSrc.releaseAccount
    _ <- accAccessDest.releaseAccount
  } yield transferResult

  private def adjust(accAccessSrc: AccountAccess, accAccessDest: AccountAccess, amount: Int): EitherT[IO, TransferFailed, TransferSuccess] = EitherT {
    if (accAccessSrc.account.balance >= amount)
      storage.update(accAccMap =>
        accAccMap
          .updated(accAccessSrc.account.accNumber, accAccessSrc.copy(account = accAccessSrc.account.copy(balance = accAccessSrc.account.balance - amount)))
          .updated(accAccessDest.account.accNumber, accAccessDest.copy(account = accAccessDest.account.copy(balance = accAccessDest.account.balance + amount)))
      ).map(_ => Right(TransferSuccess(accAccessSrc.account, accAccessDest.account, amount)))
    else
      IO(Left(TransferFailed(accAccessSrc.account, accAccessDest.account, amount, s"Not enough funds available in account number: ${accAccessSrc.account.accNumber}")))
  }

  private def getAccountAccess(accountNumber: String): EitherT[IO, AccountError, AccountAccess] = EitherT {
    storage.get.map(_.get(accountNumber).toRight[AccountError](AccountNotFound(accountNumber, s"Account Number doesn't exist: $accountNumber")))
  }

  private def generateAccountNumber: IO[String] =
    storage.get.map(accounts => (accounts.size + 1).toString)

}

object AccountInMemoryDatabase {
  def createDB: IO[AccountInMemoryDatabase] =
    Ref.of[IO, Map[String, AccountAccess]](Map.empty[String, AccountAccess])
      .map(new AccountInMemoryDatabase(_))
}

case class AccountAccess(account: Account, lock: Semaphore[IO]) {
  def acquireAccount: IO[Account] = lock.acquire.map(_ => account)
  def releaseAccount: IO[Account] = lock.release.map(_ => account)
  def isAvailable: IO[Boolean] = lock.available.map(_ > 0)
}

object AccountAccess {
  def create(account: Account)(implicit concurrent: Concurrent[IO], timer: Timer[IO]): IO[AccountAccess] =
    Semaphore[IO](1).map(lock => new AccountAccess(account, lock))
}



