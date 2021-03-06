package name.lemerdy.sebastian.bank

import java.text.NumberFormat
import java.util.Locale

case class Amount(value: Long) {

  lazy val stringValue: String = NumberFormat.getCurrencyInstance(Locale.FRANCE).format(value / 100F)

  override def toString: String = stringValue

}
