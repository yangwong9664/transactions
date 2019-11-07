package services

import javax.inject.Singleton

import models.data.Transaction

import scala.io.Source

@Singleton
class FileReader {

  //The full path to the file to import
  val fileName = "conf/resources/transactions.txt"
  //The lines of the CSV file (dropping the first to remove the header)
  val transactionslines = Source.fromFile(fileName).getLines().drop(1)
  //Here we split each line up by commas and construct Transactions
  val transactions: Seq[Transaction] = transactionslines.map { line =>
    val split = line.split(',')
    Transaction(split(0), split(1), split(2).toInt, split(3), split(4).toDouble)
  }.toSeq

}
