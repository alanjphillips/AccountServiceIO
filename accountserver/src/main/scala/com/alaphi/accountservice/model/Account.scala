package com.alaphi.accountservice.model

object Account {

  trait Payload

  trait AccountError {
    def description: String
  }

  case class Account(accNumber: String,
                     accHolderName: String,
                     balance: Int
                    ) extends Payload


  case class AccountNotFound(accNumber: String,
                             description: String
                            ) extends AccountError with Payload

}
