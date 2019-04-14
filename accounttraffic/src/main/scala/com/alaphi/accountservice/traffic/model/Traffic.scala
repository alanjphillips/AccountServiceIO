package com.alaphi.accountservice.traffic.model

object Traffic {

  case class AccountTrafficCommand(numSeedAccounts: Int, numNewAccounts: Int, numDepositsPerAccount: Int, numTransfersPerAccount: Int)

  case class AccountTrafficResult(trafficDurationMillis: Long)

}
