package name.lemerdy.sebastian.bank

case class Account(name: String, identifier: String) {

  override def equals(obj: scala.Any): Boolean = obj match {
    case account: Account => account.identifier.equals(identifier)
    case _ => false
  }

}
