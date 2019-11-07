package models.results

case class AccountCategoryTotals(accountId: String, categoryTotals: Seq[(String,BigDecimal)])
