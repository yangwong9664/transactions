import com.google.inject.{Guice, Injector}
import module.GuiceModule
import services.TransactionService
import utils.PrintHelper

object Main extends App with PrintHelper {

  lazy val injector: Injector = Guice.createInjector(new GuiceModule)

  lazy val transactionService: TransactionService = injector.getInstance(classOf[TransactionService])

  //TASK 1 calculate totals per each day
  transactionService.calculateTotalsPerDay.foreach { transaction =>
    println(s"Day ${transaction.day}: £${transaction.amount}")
  }

  //TASK 2 calculate average totals per each account
  transactionService.calculateAverageCategoriesPerAccount.foreach { account =>
    println(s"Account ${account.accountId}: ${printCategories(account.categoryTotals)}")
  }

  //TASK 3 for each day, look at up to 5 days before and get all transactions for every account during that period, and calculate averages/maxes etc.
  transactionService.rollingPeriodCalculations.foreach { transaction =>
    println(printRollingWindowCalculation(transaction))
  }

}
