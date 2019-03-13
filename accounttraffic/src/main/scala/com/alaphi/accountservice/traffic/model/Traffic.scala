package com.alaphi.accountservice.traffic.model

object Traffic {

  case class AccountTrafficCommand(numAccounts: Long, numDepositsPerAccount: Long, numTransfersPerAccount: Long)

  case class AccountTrafficResult(trafficDurationMillis: Long)

}
