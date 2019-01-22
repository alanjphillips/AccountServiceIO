package com.alaphi.accountservice.model

object Account {

  trait Payload

  trait AccountError {
    def description: String
  }

  case class AccountCreation(accHolderName: String,
                              balance: Int = 0
                            ) extends Payload

  case class Account(accNumber: String,
                     accHolderName: String,
                     balance: Int
                    ) extends Payload


  case class AccountNotFound(accNumber: String,
                             description: String
                            ) extends AccountError with Payload

  case class TransferSuccess(sourceAcc: Account,
                             destAcc: Account,
                              transferAmount: Int
                            ) extends Payload

  case class TransferFailed(sourceAcc: Account,
                             destAcc: Account,
                             transferAmount: Int,
                             description: String
                           ) extends AccountError with Payload

}
