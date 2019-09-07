package name.lemerdy.sebastian.bank.balance

import java.time.YearMonth

import name.lemerdy.sebastian.bank.Accounts
import org.scalatest.FlatSpec

class BalanceEachDaySpec extends FlatSpec {

  "cumulativeBalances" should "works" in {
    BalanceEachDay.cumulativeBalances(Accounts.Joints, YearMonth.parse("2019-03"))
  }

}
