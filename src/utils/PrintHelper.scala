package utils

import models.results.RollingTimeWindowResults

trait PrintHelper {

  def printCategories(categories: Seq[(String,BigDecimal)]) = {
    categories.map { category =>
      s"Category ${category._1}: £${category._2}"
    }.mkString(",")
  }

  def printRollingWindowCalculation(rollingTimeWindowResults: RollingTimeWindowResults) = {
    s"Day: ${rollingTimeWindowResults.day}, Account ID: ${rollingTimeWindowResults.accountId}, Maximum: ${rollingTimeWindowResults.maximum}, Average: ${rollingTimeWindowResults.average}, " +
      s"AA Total: ${rollingTimeWindowResults.aaTotal}, CC Total: ${rollingTimeWindowResults.ccTotal}, FF Total: ${rollingTimeWindowResults.ffTotal}"
  }

}
