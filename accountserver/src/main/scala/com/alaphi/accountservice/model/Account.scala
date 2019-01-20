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

  case class TransferSuccess(sourceAccNum: String,
                             destAccNum: String,
                              transferAmount: Int
                            ) extends Payload

  case class TransferFailed(sourceAccNum: String,
                             destAccNum: String,
                             transferAmount: Int,
                             description: String
                           ) extends AccountError with Payload

}
