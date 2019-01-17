package com.alaphi.accountservice.program

import cats.effect.IO
import com.alaphi.accountservice.model.Account.{Account, AccountError}
import com.alaphi.accountservice.repository.AccountRepository

class AccountProgram(accountRepository: AccountRepository) extends AccountAlgebra {

  def read(accountNumber: String): IO[Either[AccountError, Account]] =
    accountRepository.read(accountNumber)

}
