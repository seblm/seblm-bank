package name.lemerdy.sebastian.bank

case class Account(name: String, identifiers: String*) {

  override def equals(obj: scala.Any): Boolean = obj match {
    case account: Account => account.identifiers.equals(identifiers)
    case _ => false
  }

}
