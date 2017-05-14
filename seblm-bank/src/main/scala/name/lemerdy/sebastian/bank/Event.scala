package name.lemerdy.sebastian.bank

import java.time.LocalDate

import scala.util.Try

case class Event(id: Long, account: Account, date: LocalDate, libelle: Libelle, amount: Amount)

object Event {

  def apply(tokens: Array[String]): Option[Event] =
    for {
      id <- Try(tokens.head.toLong).toOption
      account <- Try(tokens(1)).toOption.flatMap(identifier => Accounts.account(identifier))
      date <- Try(LocalDate.parse(tokens(2))).toOption
      libelle <- Try(tokens(3)).map(Libelle).toOption
      amount <- Try(tokens(4).toLong).map(Amount).toOption
    } yield {
      Event(id, account, date, libelle, amount)
    }

}
