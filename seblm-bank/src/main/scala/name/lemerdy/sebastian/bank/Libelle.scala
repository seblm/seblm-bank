package name.lemerdy.sebastian.bank

case class Libelle(value: String) {
  lazy val firstLine: String = value.split("\\\\n").head
  lazy val valueWithCarriageReturns: String = value.replaceAll("\\\\n", "\n")

  override def toString: String = s"Libelle($valueWithCarriageReturns)"
}
