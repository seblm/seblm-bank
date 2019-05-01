package name.lemerdy.sebastian.bank

object Accounts {

  val All: Account = Account("All", "")

  def account(identifier: String): Option[Account] = all.find(_.identifiers.contains(identifier))

  val accounts: Iterable[Account] = Iterable(
    All,
  )

  private val all = List(
    All,
  )

}
