package name.lemerdy.sebastian.bank

import java.time.LocalDate

import scala.util.Try

case class Event(id: Long, account: Account, date: LocalDate, amount: Amount, libelle: Libelle)

object Event {

  def apply(tokens: Array[String]): Option[Event] =
    for {
      id <- Try(tokens.head.toLong).toOption
      account <- Try(tokens(1)).toOption.flatMap(identifier => Accounts.account(identifier))
      date <- Try(LocalDate.parse(tokens(2))).toOption
      amount <- Try(tokens(3).toLong).map(Amount).toOption
      libelle <- Try(tokens(4)).map(Libelle).toOption
    } yield {
      Event(id, account, date, amount, libelle)
    }

}
