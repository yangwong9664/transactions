package utils

import models.data.Transaction

import scala.math.BigDecimal.RoundingMode

trait TransactionCalculator extends Constants {

  def transactionSum(transactions: Seq[Transaction]): BigDecimal = {
    removeRemainder(transactions.map(_.transactionAmount).sum)
  }

  def transactionAverage(transactions: Seq[Transaction]): BigDecimal = {
    removeRemainder(transactions.map(_.transactionAmount).sum / transactions.length)
  }

  def transactionMax(transactions: Seq[Transaction]): BigDecimal = {
    removeRemainder(transactions.map(_.transactionAmount).max)
  }

  def categoryTotal(transactions: Seq[Transaction], categoryName: String): BigDecimal = {
    val categoryTransactions = transactions.filter(_.category == categoryName)
    removeRemainder(categoryTransactions.map(_.transactionAmount).sum)
  }

  private def removeRemainder(sum: Double): BigDecimal = BigDecimal(sum).setScale(2,RoundingMode.HALF_EVEN)

}
