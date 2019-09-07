package name.lemerdy.sebastian.bank.balance

import name.lemerdy.sebastian.bank.Accounts._
import name.lemerdy.sebastian.bank._
import name.lemerdy.sebastian.bank.balance.Balance.START

object BalanceEachEvent extends App {

  private case class CumulativeBalance(event: Event, cumulative: Amount)

  private val chronologicalOrder: (Event, Event) => Boolean = (e1, e2) => e1.date.isBefore(e2.date)

  private def printCumulativeBalance(accounts: Accounts): Unit = {
    println("" +
      s"${accounts.accounts.map(_.name).mkString(", ")}\t${accounts.accounts.flatMap(_.identifiers).mkString(", ")}\n" +
      "date\tcumulative\tcurrent\tlibelle")
    println(Events.events
      .filter(event => if (accounts.equals(Joints)) {
        !event.libelle.equals(Libelle("DEBIT CARTE BANCAIRE DIFFERE")) &&
          !event.libelle.equals(Libelle("FRAISPAIEMENTCARTEINTERNATIO.")) && accounts.accounts.contains(event.account)
      } else {
        accounts.accounts.contains(event.account)
      })
      .sortWith(chronologicalOrder)
      .scanLeft(CumulativeBalance(Event(-1, accounts.accounts.head, START, Amount(0L), Libelle("")), Amount(0L))) { case (CumulativeBalance(_, cumulative), event) =>
        CumulativeBalance(event, Amount(cumulative.value + event.amount.value))
      }
      .drop(1)
      .map(cumulativeBalance => s"${cumulativeBalance.event.date}\t${cumulativeBalance.cumulative}\t${cumulativeBalance.event.amount}\t${cumulativeBalance.event.libelle.firstLine}")
      .mkString("\n"))
  }

  printCumulativeBalance(Alls)

}
