package name.lemerdy.sebastian.bank

import org.scalatest.{FlatSpec, Matchers}

class AmountSpec extends FlatSpec with Matchers {

  "Amount" should "print values" in {
    val amount = Amount(138876)

    val formattedAmount = amount.toString

    formattedAmount should be("1\u00a0388,76\u00a0€")
  }

}
