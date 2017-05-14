package name.lemerdy.sebastian.bank.balance

import name.lemerdy.sebastian.bank.{Amount, Events}

object Balance extends App {

  Events.events(events => println(s"balance = ${Amount(events.map(_.amount.value).sum)}"))

}
