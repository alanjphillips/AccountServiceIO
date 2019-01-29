package com.alaphi.accountservice.db

import cats.data.EitherT
import cats.implicits._
import cats.effect.{Concurrent, ContextShift, IO}
import cats.effect.concurrent.{Ref, Semaphore}
import com.alaphi.accountservice.model.Account._

class AccountInMemoryDatabase private(storage: Ref[IO, Map[String, AccountAccess]])(implicit ctx: ContextShift[IO]) {

  def create(accountCreation: AccountCreation): IO[Account] = for {
    accountNumber <- generateAccountNumber
    accountAccess <- AccountAccess.create(Account(accountNumber, accountCreation.accHolderName, accountCreation.balance))
    account = accountAccess.account
    _ <- storage.update(_.updated(account.accNumber, accountAccess))
  } yield account

  def read(accountNumber: String): EitherT[IO, AccountError, Account] =
    getAccountAccess(accountNumber).map(_.account)

  def readAll: IO[Seq[Account]] =
    storage.get.map(_.values.map(_.account).toSeq)

  def deposit(accountNumber: String, amount: Int): EitherT[IO, AccountError, DepositSuccess] = for {
    accAccess <- getAccountAccess(accountNumber)
    _ <- EitherT.right(accAccess.acquireAccount)
    accountDeposit = accAccess.account.copy(balance = accAccess.account.balance + amount)
    depositResult <-
      EitherT[IO, AccountError, DepositSuccess] {
        storage.update(accAccMap =>
          accAccMap.updated(accAccess.account.accNumber, accAccess.copy(account = accountDeposit))
        ).map(_ => Right(DepositSuccess(accountDeposit, amount)))
          .guarantee(release(accAccess).void)
      }
  } yield depositResult

  def transfer(srcAccNum: String, destAccNum: String, amount: Int): EitherT[IO, AccountError, TransferSuccess] = for {
    accAccessSrc <- getAccountAccess(srcAccNum)
    accAccessDest <- getAccountAccess(destAccNum)
    _ <- EitherT.right(accAccessSrc.acquireAccount)
    _ <- EitherT.right(accAccessDest.acquireAccount)
    transferResult <- EitherT {
      adjust(accAccessSrc, accAccessDest, amount)
        .guarantee(release(accAccessSrc, accAccessDest).void)
    }
  } yield transferResult

  private def adjust(accAccessSrc: AccountAccess, accAccessDest: AccountAccess, amount: Int): IO[Either[AccountError, TransferSuccess]] =
    if (accAccessSrc.account.balance >= amount) {
      val accDebit = accAccessSrc.account.copy(balance = accAccessSrc.account.balance - amount)
      val accCredit = accAccessDest.account.copy(balance = accAccessDest.account.balance + amount)
      storage.update(accAccMap =>
        accAccMap
          .updated(accAccessSrc.account.accNumber, accAccessSrc.copy(account = accDebit))
          .updated(accAccessDest.account.accNumber, accAccessDest.copy(account = accCredit))
      ).map(_ => Right(TransferSuccess(accDebit, accCredit, amount)))
    } else
      IO(Left(TransferFailed(accAccessSrc.account, accAccessDest.account, amount, s"Not enough funds available in account number: ${accAccessSrc.account.accNumber}")))

  private def release(accAccess: AccountAccess*): IO[List[Account]] =
    accAccess.toList.map(_.releaseAccount).sequence

  private def getAccountAccess(accountNumber: String): EitherT[IO, AccountError, AccountAccess] = EitherT {
    storage.get.map(_.get(accountNumber).toRight[AccountError](AccountNotFound(accountNumber, s"Account Number doesn't exist: $accountNumber")))
  }

  private def generateAccountNumber: IO[String] =
    storage.get.map(accounts => (accounts.size + 1).toString)

}

object AccountInMemoryDatabase {
  def createDB(implicit ctx: ContextShift[IO]): IO[AccountInMemoryDatabase] =
    Ref.of[IO, Map[String, AccountAccess]](Map.empty[String, AccountAccess])
      .map(new AccountInMemoryDatabase(_))
}

case class AccountAccess(account: Account, lock: Semaphore[IO]) {
  def acquireAccount: IO[Account] = lock.acquire.map(_ => account)
  def releaseAccount: IO[Account] = lock.release.map(_ => account)
  def isAvailable: IO[Boolean] = lock.available.map(_ > 0)
}

object AccountAccess {
  def create(account: Account)(implicit ctx: Concurrent[IO]): IO[AccountAccess] =
    Semaphore[IO](1).map(lock => new AccountAccess(account, lock))
}




