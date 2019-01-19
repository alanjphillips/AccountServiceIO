package com.alaphi.accountservice.program

import cats.effect.IO
import com.alaphi.accountservice.model.Account.{Account, AccountCreation, AccountError}
import com.alaphi.accountservice.repository.AccountRepository

class AccountProgram(accountRepository: AccountRepository) extends AccountAlgebra {

  def create(accountCreation: AccountCreation): IO[Account] =
    accountRepository.create(accountCreation)

  def read(accountNumber: String): IO[Either[AccountError, Account]] =
    accountRepository.read(accountNumber)

}
