package services

import models.data.Transaction
import models.results.RollingTimeWindowResults
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.time.{Seconds, Span}
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach, Matchers, WordSpec}

class TransactionServiceSpec extends WordSpec with Matchers with ScalaFutures
  with IntegrationPatience with BeforeAndAfterEach with BeforeAndAfterAll {

  val TIME_OUT = 5
  val INTERVAL = 0.1
  implicit val defaultPatience: PatienceConfig = PatienceConfig(timeout = Span(TIME_OUT, Seconds), interval = Span(INTERVAL, Seconds))

  "calculateTotalsPerDay" should {
    "correctly calculate totals given 1 day and 1 transaction for 1 account" in {

      val fileReader = new FileReader(){
        override val transactions: Seq[Transaction] = Seq(Transaction("id","id",1,"AA",1))
      }
      val transactionService = new TransactionService(fileReader)

      transactionService.calculateTotalsPerDay.length shouldBe 1
      transactionService.calculateTotalsPerDay.map(_.amount).head shouldBe 1.0
    }

    "correctly calculate totals given 2 days, 2 categories, and 4 transactions for 2 accounts" in {

      val fileReader = new FileReader(){
        override val transactions: Seq[Transaction] = Seq(
          Transaction("t1","AA1",1,"AA",3),Transaction("t2","AA1",2,"BB",9),
          Transaction("t1","BB1",1,"BB",6),Transaction("t2","BB1",2,"BB",12))
      }
      val transactionService = new TransactionService(fileReader)

      transactionService.calculateTotalsPerDay.length shouldBe 2

      transactionService.calculateTotalsPerDay.head.day shouldBe 1
      transactionService.calculateTotalsPerDay.head.amount shouldBe 9

      transactionService.calculateTotalsPerDay(1).day shouldBe 2
      transactionService.calculateTotalsPerDay(1).amount shouldBe 21
    }
  }

  "calculateAverageCategoriesPerAccount" should {
    "calculate averages for 3 categories for 1 account" in {

      val fileReader = new FileReader(){
        override val transactions: Seq[Transaction] = Seq(
          Transaction("t1","A1",1,"AA",1),
          Transaction("t1","A1",2,"AA",2),
          Transaction("t1","A1",3,"AA",3),


          Transaction("t1","A1",1,"BB",100),
          Transaction("t1","A1",1,"BB",50),
          Transaction("t1","A1",1,"BB",75),

          Transaction("t1","A1",1,"CC",5),
          Transaction("t1","A1",1,"CC",5),
          Transaction("t1","A1",2,"CC",5)
        )
      }
      val transactionService = new TransactionService(fileReader)

      transactionService.calculateAverageCategoriesPerAccount.head.accountId shouldBe "A1"
      transactionService.calculateAverageCategoriesPerAccount.head.categoryTotals shouldBe Seq(("AA",2),("BB",75),("CC",5))
    }

    "calculate averages for 1 category for 1 account" in {

      val fileReader = new FileReader(){
        override val transactions: Seq[Transaction] = Seq(
          Transaction("t1","A1",1,"AA",1)
        )
      }
      val transactionService = new TransactionService(fileReader)

      transactionService.calculateAverageCategoriesPerAccount.head.accountId shouldBe "A1"
      transactionService.calculateAverageCategoriesPerAccount.head.categoryTotals shouldBe Seq(("AA",1))
    }

    "calculate averages for 3 categories for 3 accounts" in {

      val fileReader = new FileReader(){
        override val transactions: Seq[Transaction] = Seq(
          Transaction("t1","A1",1,"AA",1),
          Transaction("t1","A2",2,"AA",2),
          Transaction("t1","A3",3,"AA",3),


          Transaction("t1","A1",1,"BB",100),
          Transaction("t1","A2",1,"BB",50),
          Transaction("t1","A3",1,"BB",75),

          Transaction("t1","A1",1,"CC",5),
          Transaction("t1","A2",1,"CC",5),
          Transaction("t1","A3",2,"CC",5)
        )
      }
      val transactionService = new TransactionService(fileReader)

      transactionService.calculateAverageCategoriesPerAccount.map(_.accountId) shouldBe Seq("A1","A2","A3")
      transactionService.calculateAverageCategoriesPerAccount.head.categoryTotals shouldBe Seq(("AA",1.00),("BB",100.00),("CC",5.00))
    }

  "calculate averages for 1 category for 3 accounts" in {

    val fileReader = new FileReader(){
      override val transactions: Seq[Transaction] = Seq(
        Transaction("t1","A1",1,"AA",1),
        Transaction("t1","B1",1,"AA",100),
        Transaction("t1","C1",1,"AA",5),
      )
    }
    val transactionService = new TransactionService(fileReader)

    transactionService.calculateAverageCategoriesPerAccount.map(_.accountId) shouldBe Seq("A1","B1","C1")

    transactionService.calculateAverageCategoriesPerAccount.head.categoryTotals shouldBe Seq(("AA",1.0))
    transactionService.calculateAverageCategoriesPerAccount(1).categoryTotals shouldBe Seq(("AA",100.0))
    transactionService.calculateAverageCategoriesPerAccount(2).categoryTotals shouldBe Seq(("AA",5.0))
  }
}

  "rollingPeriodCalculations" should {
    "identify 1 account, and calculate a 10 day period" in {

      val fileReader = new FileReader(){
        override val transactions: Seq[Transaction] = Seq(
          Transaction("t1","A1",1,"AA",1),
          Transaction("t1","A1",2,"BB",1),//0
          Transaction("t1","A1",3,"AA",1),//1
          Transaction("t1","A1",4,"BB",1),//2
          Transaction("t1","A1",5,"AA",1),//3
          Transaction("t1","A1",6,"CC",1),//4
          Transaction("t1","A1",7,"AA",1),//5
          Transaction("t1","A1",8,"AA",5),//6
          Transaction("t1","A1",9,"AA",1),//7
          Transaction("t1","A1",10,"AA",1)//8
        )
      }
      val transactionService = new TransactionService(fileReader)

      transactionService.rollingPeriodCalculations.head.accountId shouldBe "A1"
      transactionService.rollingPeriodCalculations.length shouldBe 9

      transactionService.rollingPeriodCalculations(8).maximum shouldBe 5.0
      transactionService.rollingPeriodCalculations(3).maximum shouldBe 1.0

      transactionService.rollingPeriodCalculations(8).aaTotal shouldBe 8.0
      transactionService.rollingPeriodCalculations(3).aaTotal shouldBe 2.0

      transactionService.rollingPeriodCalculations(8).average shouldBe 1.8
      transactionService.rollingPeriodCalculations(3).average shouldBe 1.0
    }

    "identify 2 accounts, and calculate a 5 day period" in {

      val fileReader = new FileReader(){
        override val transactions: Seq[Transaction] = Seq(
          Transaction("t1","A1",1,"AA",10),
          Transaction("t1","A1",2,"CC",1),//0
          Transaction("t1","A2",3,"AA",1),//1
          Transaction("t1","A1",4,"CC",2),//2
          Transaction("t1","A2",5,"FF",1),//3
        )
      }
      val transactionService = new TransactionService(fileReader)

      transactionService.rollingPeriodCalculations shouldBe Seq(
          RollingTimeWindowResults(2,"A1",10.00,10.00,10.00,0.00,0.00),
          RollingTimeWindowResults(3,"A1",10.00,5.50,10.00,1.00,0.00),
          RollingTimeWindowResults(4,"A1",10.00,5.50,10.00,1.00,0.00),
          RollingTimeWindowResults(4,"A2",1.00,1.00,1.00,0.00,0.00),
          RollingTimeWindowResults(5,"A1",10.00,4.33,10.00,3.00,0.00),
          RollingTimeWindowResults(5,"A2",1.00,1.00,1.00,0.00,0.00))
    }
  }

}
