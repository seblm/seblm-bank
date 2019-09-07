package name.lemerdy.sebastian.bank.operations

import name.lemerdy.sebastian.bank.Accounts.{All, Joints}
import name.lemerdy.sebastian.bank.{Account, Event, Libelle}

case class AccountsSelection(filter: Event => Boolean, accounts: Account*)

object AccountsSelection {

  val joints = AccountsSelection(
    event => event.libelle != Libelle("DEBIT CARTE BANCAIRE DIFFERE"),
    All
  )

  val joint = AccountsSelection(
    _ => true,
    Joints.accounts: _*
  )

}
