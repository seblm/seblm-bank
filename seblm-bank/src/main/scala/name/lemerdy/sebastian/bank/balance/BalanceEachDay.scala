package name.lemerdy.sebastian.bank.balance

import java.time.LocalDate

import name.lemerdy.sebastian.bank.Accounts._
import name.lemerdy.sebastian.bank._

object BalanceEachDay extends App {

  private val chronologicalOrder: (((LocalDate, Any), (LocalDate, Any)) => Boolean) = {
    case ((d1, _), (d2, _)) => d1.isBefore(d2)
  }

  private def printCumulativeBalance(account: Account) = {
    println("" +
      s"${account.name}\t${account.identifier}\n" +
      "date\tcumulative\tcurrent\tlibelle")
    Events.events
      .filter(event => if (account.equals(All)) {
        !event.libelle.equals(Libelle("DEBIT CARTE BANCAIRE DIFFERE"))
      } else {
        event.account.equals(account)
      })
      .groupBy(_.date)
      .toSeq
      .sortWith(chronologicalOrder)
      .scanLeft(CumulativeBalance(Event(-1, account, LocalDate.parse("2017-03-26"), Amount(0L), Libelle("")), Amount(0L))) { case ((CumulativeBalance(_, cumulative)), (date, eventsSameDate)) =>
        val amountSameDay = eventsSameDate.map(_.amount.value).sum
        CumulativeBalance(Event(0, account, date, Amount(amountSameDay), Libelle(eventsSameDate.map(_.libelle.firstLine).mkString(", "))), Amount(cumulative.value + amountSameDay))
      }
      .drop(1)
      .foreach(cumulativeBalance => println(s"${cumulativeBalance.event.date}\t${cumulativeBalance.cumulative}\t${cumulativeBalance.event.amount}\t${cumulativeBalance.event.libelle.firstLine}"))
  }

  printCumulativeBalance(All)

}
