package name.lemerdy.sebastian.bank.operations

import java.time.{LocalDate, YearMonth}

import name.lemerdy.sebastian.bank.balance.Balance
import name.lemerdy.sebastian.bank.{Account, Amount, Event, Libelle}
import org.scalatest.FlatSpec
import org.scalatest.Matchers._

class OperationsSpec extends FlatSpec {

  "operations" should "be built from events" in withEvents { (main, creditCard, events) =>
    val operations: Seq[Operation] =
      Operations.from(events, AccountsSelection(_.libelle != Libelle("credit card operations"), main, creditCard))

    operations should contain inOrderOnly(
      Operation(LocalDate.parse("2019-03-10"), Libelle("sneakers"), Amount(-8250), Balance(108517)),
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
      DailyOperation(LocalDate.parse("2019-03-10"), Amount(-8250), Balance(108517)),
      DailyOperation(LocalDate.parse("2019-03-03"), Amount(-2000), Balance(116767)),
      DailyOperation(LocalDate.parse("2019-03-02"), Amount(-6233), Balance(118767)),
      DailyOperation(LocalDate.parse("2019-03-01"), Amount(125000), Balance(125000)),
    )
  }

  it should "be projected for each day of a month" in withEvents { (main, creditCard, events) =>
    val operations: Seq[Balance] =
      Operations.monthly(events, AccountsSelection(_.libelle != Libelle("credit card operations"), main, creditCard), YearMonth.of(2019, 3))

    operations.reverse.zipWithIndex.map { case (balance, day) => (balance, day + 1) }.reverse should contain inOrderOnly(
      (Balance(108517), /* 2019-03- */ 30),
      (Balance(108517), /* 2019-03- */ 29),
      (Balance(108517), /* 2019-03- */ 28),
      (Balance(108517), /* 2019-03- */ 27),
      (Balance(108517), /* 2019-03- */ 26),
      (Balance(108517), /* 2019-03- */ 25),
      (Balance(108517), /* 2019-03- */ 24),
      (Balance(108517), /* 2019-03- */ 23),
      (Balance(108517), /* 2019-03- */ 22),
      (Balance(108517), /* 2019-03- */ 21),
      (Balance(108517), /* 2019-03- */ 20),
      (Balance(108517), /* 2019-03- */ 19),
      (Balance(108517), /* 2019-03- */ 18),
      (Balance(108517), /* 2019-03- */ 17),
      (Balance(108517), /* 2019-03- */ 16),
      (Balance(108517), /* 2019-03- */ 15),
      (Balance(108517), /* 2019-03- */ 14),
      (Balance(108517), /* 2019-03- */ 13),
      (Balance(108517), /* 2019-03- */ 12),
      (Balance(108517), /* 2019-03- */ 11),
      (Balance(108517), /* 2019-03- */ 10),
      (Balance(116767), /* 2019-03-0 */ 9),
      (Balance(116767), /* 2019-03-0 */ 8),
      (Balance(116767), /* 2019-03-0 */ 7),
      (Balance(116767), /* 2019-03-0 */ 6),
      (Balance(116767), /* 2019-03-0 */ 5),
      (Balance(116767), /* 2019-03-0 */ 4),
      (Balance(116767), /* 2019-03-0 */ 3),
      (Balance(118767), /* 2019-03-0 */ 2),
      (Balance(125000), /* 2019-03-0 */ 1),
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
      Event(4L, mainAccoun, LocalDate.parse("2019-03-20"), Amount(-14483), Libelle("credit card operations")),
      Event(5L, creditCard, LocalDate.parse("2019-03-02"), Amount(-1234), Libelle("shopping")),
      Event(6L, creditCard, LocalDate.parse("2019-03-02"), Amount(-4999), Libelle("flowers")),
      Event(7L, creditCard, LocalDate.parse("2019-03-10"), Amount(-8250), Libelle("sneakers")),
    )

    testCode(mainAccoun, creditCard, events)
  }

}
