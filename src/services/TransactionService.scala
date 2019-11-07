package services

import javax.inject.Singleton

import com.google.inject.Inject
import models.data.Transaction
import models.results.{AccountCategoryTotals, RollingTimeWindowResults, TotalPerDay}
import utils.{Constants, Sorter, TransactionCalculator}


@Singleton
class TransactionService @Inject()(fileReader: FileReader) extends TransactionCalculator with Sorter with Constants {

  //TASK 1

  def calculateTotalsPerDay: Seq[TotalPerDay] = {
    //get transactions, group them by day, and find the sum of transactions per day
    fileReader.transactions.groupBy(_.transactionDay).map { transactions =>
      TotalPerDay(transactions._1,transactionSum(transactions._2))
    }.toSeq.sort
  }

  //TASK 2

  def calculateAverageCategoriesPerAccount: Seq[AccountCategoryTotals] = {
    //get transactions, group by account ID, and find the average of each category per account
    fileReader.transactions.groupBy(_.accountId).map { transactions =>
      AccountCategoryTotals(transactions._1,categoryAverages(transactions._2))
    }.toSeq.sort
  }

  private def categoryAverages(transactions: Seq[Transaction]) = {
    //separate any categories that an account has, and calculate the average per category
    transactions.groupBy(_.category).map { res =>
      (res._1,transactionAverage(res._2))
    }.toSeq.sort
  }

  //TASK 3

  def rollingPeriodCalculations = {
    //make groups of transactions per day, and order them by day before calculation, then order by day and accountID after the calculation to println in order
    rollingTimeWindowCalculation(fileReader.transactions.groupBy(_.transactionDay).toSeq.sortBy(_._1)).sort.sortBy(_.day)
  }

  private def rollingTimeWindowCalculation(transaction: Seq[(Int,Seq[Transaction])], results: Seq[RollingTimeWindowResults] = Seq.empty): Seq[RollingTimeWindowResults] = {
    //recursively work from the end of the list to the start, as they are ordered by day
    if(transaction.isEmpty) results else {
      val dataRange: Seq[(String, Seq[Transaction])] = transaction.init.takeRight(ROLLING_TIME_WINDOW).flatMap(_._2).groupBy(_.accountId).toSeq
      //drop the last element e.g day 29, and take the last 5 instead e.g day 24-28, then group transactions by accountId
      val result: Seq[RollingTimeWindowResults] = dataRange.map(account => RollingTimeWindowResults(transaction.last._1, account._1, transactionMax(account._2), transactionAverage(account._2),
        categoryTotal(account._2, CATEGORY_AA), categoryTotal(account._2, CATEGORY_CC), categoryTotal(account._2, CATEGORY_FF)))
      //now that transactions are grouped by account, iterate through all transactions per account and do calculations for each field
      rollingTimeWindowCalculation(transaction.init,result ++ results)
      //drop the last day e.g 29, then repeat for the second last number, e.g 28, and keep going down until all days have been calculated
    }
  }

}
