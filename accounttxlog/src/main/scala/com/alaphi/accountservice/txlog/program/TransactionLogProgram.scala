package com.alaphi.accountservice.txlog.program

import cats.effect.IO
import com.alaphi.accountservice.model.Accounts.Payload
import com.alaphi.accountservice.txlog.repository.TransactionLogRepository

class TransactionLogProgram(txLogRepository: TransactionLogRepository) extends TransactionLogAlgebra {

  def read(offset: Int): IO[Payload] = txLogRepository.read(offset = offset)

  def readAll: IO[Seq[Payload]] = txLogRepository.readAll

  def size: IO[Int] = txLogRepository.size

}
