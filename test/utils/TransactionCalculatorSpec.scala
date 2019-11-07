package utils

import models.data.Transaction
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.time.{Seconds, Span}
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach, Matchers, WordSpec}

class TransactionCalculatorSpec extends WordSpec with Matchers with ScalaFutures
  with IntegrationPatience with BeforeAndAfterEach with BeforeAndAfterAll {

  val TIME_OUT = 5
  val INTERVAL = 0.1
  implicit val defaultPatience: PatienceConfig = PatienceConfig(timeout = Span(TIME_OUT, Seconds), interval = Span(INTERVAL, Seconds))

  val calculator = new TransactionCalculator {}

  "transactionAverage" should {
    "calculate an average and remove decimals correctly" in {
      calculator.transactionAverage(Seq(
        Transaction("a","A",1,"AA",10.000000000001),
        Transaction("a","A",1,"AA",10.000000000001),
        Transaction("a","A",1,"AA",10.000000000001),
        Transaction("a","A",1,"AA",5.000000000001))) shouldBe 8.75
    }
  }

  "categoryTotal" should {
    "calculate a total per category and remove decimals correctly" in {
      val res = calculator.categoryTotal(Seq(
        Transaction("a","A",1,"AA",10.000000000001),
        Transaction("a","A",1,"BB",10.000000000001),
        Transaction("a","A",1,"AA",10.000000000001),
        Transaction("a","A",1,"AA",5.000000000001)),"AA")

       res shouldBe 25

      val res1 = calculator.categoryTotal(Seq(
        Transaction("a","A",1,"AA",10.000000000001),
        Transaction("a","A",1,"BB",10.1),
        Transaction("a","A",1,"AA",10.000000000001),
        Transaction("a","A",1,"AA",5.000000000001)),"BB")

      res1 shouldBe 10.1
    }
  }

}
