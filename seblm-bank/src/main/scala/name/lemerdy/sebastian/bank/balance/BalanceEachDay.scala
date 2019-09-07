package name.lemerdy.sebastian.bank.balance

import java.time.YearMonth

import name.lemerdy.sebastian.bank.Accounts.All
import name.lemerdy.sebastian.bank.balance.Balance.START
import name.lemerdy.sebastian.bank._

object BalanceEachDay extends App {

  def cumulativeBalances(accounts: Accounts, month: YearMonth): Seq[Amount] = {
    val days = Seq.tabulate(month.lengthOfMonth() - 1)(day => month.atDay(day + 1))
    val balances = cumulativeBalances(event => accounts.accounts.contains(event.account))
    days.map { day =>
      balances.find(_.date == day)
        .orElse(balances.takeWhile(_.date.isBefore(day)).lastOption)
        .map(_.cumulative)
        .getOrElse(Amount(0L))
    }
  }

  private def cumulativeBalances(filter: Event => Boolean): Seq[CumulativeBalance] = Events.events
    .filter(filter)
    .groupBy(_.date)
    .toSeq
    .sortBy(_._1)(Ordering.by(_.toEpochDay))
    .scanLeft(CumulativeBalance(START, Amount(0L), Libelle(""), Amount(0L))) { case (CumulativeBalance(_, _, _, cumulative), (date, eventsSameDate)) =>
      val amountSameDay = eventsSameDate.map(_.amount.value).sum
      CumulativeBalance(date, Amount(amountSameDay), Libelle(eventsSameDate.map(_.libelle.firstLine).mkString(", ")), Amount(cumulative.value + amountSameDay))
    }

  private def printCumulativeBalance(account: Account): Unit = {
    println("" +
      s"${account.name}\t${account.identifiers}\n" +
      "date\tcumulative\tcurrent\tlibelle")
    println(cumulativeBalances(event => if (account.equals(All)) {
      !event.libelle.equals(Libelle("DEBIT CARTE BANCAIRE DIFFERE"))
    } else {
      event.account.equals(account)
    })
      .drop(1)
      .map(columns)
      .mkString("\n"))
  }

  private def columns(cumulativeBalance: CumulativeBalance): String =
    s"${cumulativeBalance.date}\t${cumulativeBalance.cumulative}\t${cumulativeBalance.amount}\t${cumulativeBalance.libelle.firstLine}"

  printCumulativeBalance(All)

}
