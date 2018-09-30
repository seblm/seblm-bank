package name.lemerdy.sebastian.bank.balance

import java.time.LocalDate

import name.lemerdy.sebastian.bank.Accounts._
import name.lemerdy.sebastian.bank._

object BalanceEachEvent extends App {

  private case class CumulativeBalance(event: Event, cumulative: Amount)

  private val chronologicalOrder: (Event, Event) => Boolean = (e1, e2) => e1.date.isBefore(e2.date)

  private def printCumulativeBalance(account: Account): Unit = {
    println("" +
      s"${account.name}\t${account.identifier}\n" +
      "date\tcumulative\tcurrent\tlibelle")
    Events.events
      .filter(event => if (account.equals(All)) {
        !event.libelle.equals(Libelle("DEBIT CARTE BANCAIRE DIFFERE"))
      } else {
        event.account.equals(account)
      })
      .sortWith(chronologicalOrder)
      .scanLeft(CumulativeBalance(Event(-1, account, LocalDate.parse("2017-03-26"), Amount(0L), Libelle("")), Amount(0L))) { case ((CumulativeBalance(_, cumulative)), event) =>
        CumulativeBalance(event, Amount(cumulative.value + event.amount.value))
      }
      .drop(1)
      .foreach(cumulativeBalance => println(s"${cumulativeBalance.event.date}\t${cumulativeBalance.cumulative}\t${cumulativeBalance.event.amount}\t${cumulativeBalance.event.libelle.firstLine}"))
  }

  printCumulativeBalance(All)

}
