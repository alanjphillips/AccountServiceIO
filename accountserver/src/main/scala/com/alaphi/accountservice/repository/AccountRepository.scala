package com.alaphi.accountservice.repository

import cats.effect.IO
import com.alaphi.accountservice.model.Account._

trait AccountRepository {
  def create(accountCreation: AccountCreation): IO[Account]
  def read(accountNumber: String): IO[Either[AccountError, Account]]
}
