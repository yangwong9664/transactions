package utils

import models.results.{AccountCategoryTotals, RollingTimeWindowResults, TotalPerDay}

trait Sorter {
  //hides the sorting code so that the main implementations have less code and are cleaner
  trait Sort[T] {
    def sorter(collection: Seq[T]): Seq[T]
  }

  implicit class Sorter[T](collection: Seq[T]) {
    def sort(implicit sort: Sort[T]) = sort.sorter(collection)
  }

  implicit val sortByTransactionsPerDay = new Sort[TotalPerDay] {
    def sorter(collection: Seq[TotalPerDay]): Seq[TotalPerDay] = collection.sortBy(_.day)
  }

  implicit val sortByAccountId = new Sort[AccountCategoryTotals] {
    def sorter(collection: Seq[AccountCategoryTotals]): Seq[AccountCategoryTotals] = {
      collection.sortBy(_.accountId.filter(char => !char.isDigit)).sortBy(_.accountId.filter(_.isDigit).toInt)
    }
  }

  implicit val sortByAccountIdTransaction = new Sort[RollingTimeWindowResults] {
    def sorter(collection: Seq[RollingTimeWindowResults]): Seq[RollingTimeWindowResults] = {
      collection.sortBy(_.accountId.filter(char => !char.isDigit)).sortBy(_.accountId.filter(_.isDigit).toInt)
    }
  }

  implicit val sortByCategory = new Sort[(String,BigDecimal)] {
    def sorter(collection: Seq[(String,BigDecimal)]): Seq[(String,BigDecimal)] = collection.sortBy(_._1)
  }

}
