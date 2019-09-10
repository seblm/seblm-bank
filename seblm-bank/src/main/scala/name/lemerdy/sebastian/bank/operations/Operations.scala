package name.lemerdy.sebastian.bank.operations

import java.time.YearMonth

import name.lemerdy.sebastian.bank.balance.Balance
import name.lemerdy.sebastian.bank.{Amount, Event, Events}

object Operations extends App {

  private def filter(events: Seq[Event], accounts: AccountsSelection): Seq[Event] =
    events
      .filter(event => accounts.accounts.contains(event.account))
      .filter(accounts.filter)
      .sortBy(_.date)(Ordering.by(_.toEpochDay))

  def from(events: Seq[Event], accounts: AccountsSelection): Seq[Operation] =
    filter(events, accounts)
      .foldLeft(Seq.empty[Operation]) { case (operations, event) =>
        val oldBalance = operations.lastOption.map(_.balance.value).getOrElse(0L)
        val newBalance = Balance(oldBalance + event.amount.value)
        operations :+ Operation(event.date, event.libelle, event.amount, newBalance)
      }
      .reverse

  def daily(events: Seq[Event], accounts: AccountsSelection): Seq[DailyOperation] =
    filter(events, accounts)
      .foldLeft(Seq.empty[DailyOperation]) { case (operations, event) =>
        val oldBalance = operations.lastOption.map(_.balance.value).getOrElse(0L)
        val newBalance = Balance(oldBalance + event.amount.value)
        operations match {
          case init :+ last if last.date == event.date =>
            init :+ DailyOperation(event.date, Amount(last.amount.value + event.amount.value), newBalance)
          case _ =>
            operations :+ DailyOperation(event.date, Amount(event.amount.value), newBalance)
        }
      }
      .reverse

  def monthly(events: Seq[Event], accounts: AccountsSelection, month: YearMonth): Seq[Balance] = {
    val operations = daily(events, accounts)
    val initialBalance = operations.lastOption.map(_.oldBalance).getOrElse(Balance(0))
    val days = Seq.tabulate(month.lengthOfMonth() - 1)(day => month.atDay(day + 1)).reverse
    val amounts = days.map(day => operations.find(_.date == day).map(_.amount).getOrElse(Amount(0)))
    amounts.reverse.scanLeft(initialBalance) {
      case (balance, amount) => Balance(balance.value + amount.value)
    }.tail.reverse
  }

  println(Operations.from(Events.events, AccountsSelection.joint)
    .map(op =>
      s"${op.date}\t${op.balance}\t${op.amount}\t${op.label.value.replaceAll("""\\n""", " ")}"
    ).mkString("\n"))

}
