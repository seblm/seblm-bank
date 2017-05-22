package name.lemerdy.sebastian.bank

object Accounts {

  val All: Account = Account("All", "")

  def account(identifier: String): Option[Account] = identifier match {
    case _ => None
  }

  val accounts: Iterable[Account] = Iterable(
    All)

}
