package com.alaphi.accountservice.repository

import cats.effect.IO
import com.alaphi.accountservice.model.Account._

trait AccountRepository {
  def read(accountNumber: String): IO[Either[AccountError, Account]]
}
