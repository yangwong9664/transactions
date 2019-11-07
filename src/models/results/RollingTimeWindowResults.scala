package models.results

case class RollingTimeWindowResults(day: Int,
                                    accountId: String,
                                    maximum: BigDecimal,
                                    average: BigDecimal,
                                    aaTotal: BigDecimal,
                                    ccTotal: BigDecimal,
                                    ffTotal: BigDecimal)
