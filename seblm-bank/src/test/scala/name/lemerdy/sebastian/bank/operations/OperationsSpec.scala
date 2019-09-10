package name.lemerdy.sebastian.bank.operations

import java.time.LocalDate

import name.lemerdy.sebastian.bank.balance.Balance
import name.lemerdy.sebastian.bank.{Account, Amount, Event, Libelle}
import org.scalatest.FlatSpec
import org.scalatest.Matchers._

class OperationsSpec extends FlatSpec {

  "operations" should "be built from events" in withEvents { (main, creditCard, events) =>
    val operations: Seq[Operation] =
      Operations.from(events, AccountsSelection(_.libelle != Libelle("credit card operations"), main, creditCard))

    operations should contain inOrderOnly(
      Operation(LocalDate.parse("2019-03-03"), Libelle("credit transfer"), Amount(-2000), Balance(116767)),
      Operation(LocalDate.parse("2019-03-02"), Libelle("flowers"), Amount(-4999), Balance(118767)),
      Operation(LocalDate.parse("2019-03-02"), Libelle("shopping"), Amount(-1234), Balance(123766)),
      Operation(LocalDate.parse("2019-03-01"), Libelle("initial balance"), Amount(125000), Balance(125000)),
    )
  }

  it should "be aggregated on a daily basis" in withEvents { (main, creditCard, events) =>
    val operations: Seq[DailyOperation] =
      Operations.daily(events, AccountsSelection(_.libelle != Libelle("credit card operations"), main, creditCard))

    operations should contain inOrderOnly(
      DailyOperation(LocalDate.parse("2019-03-03"), Amount(-2000), Balance(116767)),
      DailyOperation(LocalDate.parse("2019-03-02"), Amount(-6233), Balance(118767)),
      DailyOperation(LocalDate.parse("2019-03-01"), Amount(125000), Balance(125000)),
    )
  }

  private def withEvents(testCode: (Account, Account, Seq[Event]) => Any): Unit = {
    val savingsAcc: Account = Account("savings    ", "0001")
    val mainAccoun: Account = Account("main       ", "0002")
    val creditCard: Account = Account("credit card", "0003")
    val events: Seq[Event] = Seq(
      Event(1L, savingsAcc, LocalDate.parse("2019-03-01"), Amount(298469), Libelle("initial balance")),
      Event(2L, mainAccoun, LocalDate.parse("2019-03-01"), Amount(125000), Libelle("initial balance")),
      Event(3L, mainAccoun, LocalDate.parse("2019-03-03"), Amount(-2000), Libelle("credit transfer")),
      Event(4L, mainAccoun, LocalDate.parse("2019-03-20"), Amount(-6233), Libelle("credit card operations")),
      Event(5L, creditCard, LocalDate.parse("2019-03-02"), Amount(-1234), Libelle("shopping")),
      Event(6L, creditCard, LocalDate.parse("2019-03-02"), Amount(-4999), Libelle("flowers")),
    )

    testCode(mainAccoun, creditCard, events)
  }

}
