package name.lemerdy.sebastian.bank.operations

import java.time.LocalDate

import name.lemerdy.sebastian.bank.Amount
import name.lemerdy.sebastian.bank.balance.Balance

case class DailyOperation(date: LocalDate, amount: Amount, balance: Balance) {
  lazy val oldBalance: Balance = Balance(balance.value - amount.value)
}
