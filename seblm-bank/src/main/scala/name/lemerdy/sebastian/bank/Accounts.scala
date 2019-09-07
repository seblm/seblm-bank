package name.lemerdy.sebastian.bank

case class Accounts(accounts: Account*)

object Accounts {

  val All: Account = Account("All", "")

  val Joints: Accounts = Accounts(All)

  val Alls: Accounts = Accounts(All)

  def account(identifier: String): Option[Account] = all.find(_.identifiers.contains(identifier))

  val accounts: Iterable[Account] = Iterable(
    All,
  )

  private val all = List(
    All,
  )

}
