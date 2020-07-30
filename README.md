# transactions

You have been provided with a text file in comma separated format of 991 transactions spread over a month. The transactions are for multiple accounts and there are multiple types of transaction. 

# Question 1
Calculate the total transaction value for all transactions for each day.
The output should contain one line for each day and each line should include the day and the total value.

# Question 2
Calculate the average value of transactions per account for each type of transaction (there are seven in total).
The output should contain one line per account, each line should include the account id and the average value for each transaction type (ie 7 fields containing the average values).

# Question 3
For each day, calculate statistics for each account number for the previous five days of transactions, not including transactions from the day statistics are being calculated for. For example, on day 10 you should consider only the transactions from days 5 to 9 (this is called a rolling time window of five days). The statistics we require to be calculated are:
• The maximum transaction value in the previous 5 days of transactions per account
• The average transaction value of the previous 5 days of transactions per account
• The total transaction value of transactions types “AA”, “CC” and “FF” in the previous 5 days per
account
The output should contain one line per day per account id and each line should contain each of the calculated statistics.
