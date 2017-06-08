package name.lemerdy.sebastian.bank.infrastructure

import java.nio.file.{Files, Paths}
import java.time.LocalDate

import akka.http.scaladsl.model.StatusCodes.{NotFound, OK}
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpResponse}
import akka.http.scaladsl.server.{HttpApp, Route}
import name.lemerdy.sebastian.bank.Accounts.All
import name.lemerdy.sebastian.bank._
import name.lemerdy.sebastian.bank.balance.CumulativeBalance

object WebServer extends HttpApp with App {

  private val chronologicalOrder: (((LocalDate, Any), (LocalDate, Any)) => Boolean) = {
    case ((d1, _), (d2, _)) => d1.isBefore(d2)
  }

  private def cumulativeBalance(account: Account): String = {
    Events.events
      .filter(event => if (account.equals(All)) {
        !event.libelle.equals(Libelle("DEBIT CARTE BANCAIRE DIFFERE"))
      } else {
        event.account.equals(account)
      })
      .groupBy(_.date)
      .toSeq
      .sortWith(chronologicalOrder)
      .scanLeft(CumulativeBalance(Event(-1, account, LocalDate.parse("1970-01-01"), Amount(0L), Libelle("")), Amount(0L))) { case ((CumulativeBalance(_, cumulative)), (date, eventsSameDate)) =>
        val amountSameDay = eventsSameDate.map(_.amount.value).sum
        CumulativeBalance(Event(0, account, date, Amount(amountSameDay), Libelle(eventsSameDate.map(_.libelle.firstLine).mkString(", "))), Amount(cumulative.value + amountSameDay))
      }
      .drop(1)
      .map(cumulativeBalance => s"[${cumulativeBalance.event.date.toEpochDay * 24 * 60 * 60 * 1000}, ${cumulativeBalance.cumulative.value / 100F}]")
      .mkString(", \n")
  }

  private def cumulativeBalances(): String = {
    Accounts.accounts.map(account =>
      s"""
         |{
         |  name: '${account.name}',
         |  data: [${cumulativeBalance(account)}]
         |}
       """.stripMargin)
      .mkString(", ")
  }

  protected override val routes: Route =
    pathSingleSlash {
      get {
        complete(HttpResponse(OK, entity = HttpEntity(ContentTypes.`text/html(UTF-8)`, Files.readAllBytes(Paths.get("web/index.html")))))
      }
    } ~
      path("jsonp") {
        parameter("callback") { callback =>
          get {
            complete(HttpResponse(OK, entity = s"$callback([${cumulativeBalances()}])"))
          }
        }
      } ~ path(Remaining) { remainingPath =>
      get {
        val file = Paths.get(remainingPath)
        if (file.toFile.exists()) {
          complete(HttpResponse(OK, entity = Files.readAllBytes(file)))
        } else {
          complete(NotFound)
        }
      }
    }

  startServer("localhost", 8080)

}
