package name.lemerdy.sebastian.bank.balance

import name.lemerdy.sebastian.bank.{Amount, Events}

object Balance extends App {

  println(s"balance = ${Amount(Events.events.map(_.amount.value).sum)}")

}
