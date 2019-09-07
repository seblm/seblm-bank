package name.lemerdy.sebastian.bank

case class Libelle(value: String) {
  lazy val firstLine: String = value.split("\\\\n").head
  lazy val valueWithCarriageReturns: String = valueSplitBy("\n")

  def valueSplitBy(separator: String): String = value.replaceAll("\\\\n", separator)

  override def toString: String = s"Libelle($valueWithCarriageReturns)"
}
