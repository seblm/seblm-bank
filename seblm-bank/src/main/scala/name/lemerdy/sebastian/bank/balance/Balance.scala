package name.lemerdy.sebastian.bank.balance

import java.text.NumberFormat
import java.time.LocalDate
import java.util.Locale

case class Balance(value: Long) {

  lazy val stringValue: String = NumberFormat.getCurrencyInstance(Locale.FRANCE).format(value / 100F)

  override def toString: String = stringValue

}

object Balance {

  lazy val START: LocalDate = LocalDate.of(2016, 11, 14)

}