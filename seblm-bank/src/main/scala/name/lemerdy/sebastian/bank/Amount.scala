package name.lemerdy.sebastian.bank

import java.text.NumberFormat

case class Amount(value: Long) {

  private lazy val stringValue = NumberFormat.getCurrencyInstance.format(value / 100F)

  override def toString: String = stringValue

}
