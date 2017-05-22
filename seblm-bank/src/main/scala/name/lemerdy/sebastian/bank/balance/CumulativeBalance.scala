package name.lemerdy.sebastian.bank.balance

import name.lemerdy.sebastian.bank.{Amount, Event}

case class CumulativeBalance(event: Event, cumulative: Amount)
