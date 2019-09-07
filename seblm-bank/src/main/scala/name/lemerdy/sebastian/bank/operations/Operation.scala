package name.lemerdy.sebastian.bank.operations

import java.time.LocalDate

import name.lemerdy.sebastian.bank.balance.Balance
import name.lemerdy.sebastian.bank.{Amount, Libelle}

case class Operation(date: LocalDate, label: Libelle, amount: Amount, balance: Balance)
