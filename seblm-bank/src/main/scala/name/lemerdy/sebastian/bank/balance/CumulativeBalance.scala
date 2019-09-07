package name.lemerdy.sebastian.bank.balance

import java.time.LocalDate

import name.lemerdy.sebastian.bank.{Amount, Libelle}

case class CumulativeBalance(date: LocalDate, amount: Amount, libelle: Libelle, cumulative: Amount)
