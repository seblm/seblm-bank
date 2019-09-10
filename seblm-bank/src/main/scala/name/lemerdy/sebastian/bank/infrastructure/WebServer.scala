package name.lemerdy.sebastian.bank.infrastructure

import java.nio.file.{Files, Paths}
import java.time.{LocalDate, YearMonth}

import akka.http.scaladsl.model.StatusCodes.{BadRequest, NotFound, OK}
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpResponse}
import akka.http.scaladsl.server.{HttpApp, Route}
import name.lemerdy.sebastian.bank.Accounts.{All, Joints}
import name.lemerdy.sebastian.bank._
import name.lemerdy.sebastian.bank.balance.{BalanceEachDay, CumulativeBalance}
import name.lemerdy.sebastian.bank.operations.{AccountsSelection, Operations}

import scala.util.Try

object WebServer extends HttpApp with App {

  private val chronologicalOrder: ((LocalDate, Any), (LocalDate, Any)) => Boolean = {
    case ((d1, _), (d2, _)) => d1.isBefore(d2)
  }

  private def cumulativeBalance(account: Account): String =
    Events.events
      .filter(event => if (account.equals(All)) {
        !event.libelle.equals(Libelle("DEBIT CARTE BANCAIRE DIFFERE"))
      } else {
        event.account.equals(account)
      })
      .groupBy(_.date)
      .toSeq
      .sortWith(chronologicalOrder)
      .scanLeft(CumulativeBalance(LocalDate.parse("1970-01-01"), Amount(0L), Libelle(""), Amount(0L))) { case (CumulativeBalance(_, _, _, cumulative), (date, eventsSameDate)) =>
        val amountSameDay = eventsSameDate.map(_.amount.value).sum
        CumulativeBalance(date, Amount(amountSameDay), Libelle(eventsSameDate.map(_.libelle.firstLine).mkString(", ")), Amount(cumulative.value + amountSameDay))
      }
      .drop(1)
      .map(cumulativeBalance => s"[${cumulativeBalance.date.toEpochDay * 24 * 60 * 60 * 1000}, ${cumulativeBalance.cumulative.value / 100F}]")
      .mkString(", \n")

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

  private def at(yearMonth: YearMonth)(date: LocalDate): Boolean =
    date.getYear == yearMonth.getYear && date.getMonth == yearMonth.getMonth

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
      } ~
      pathPrefix(Segment) { accountsSelectionFromPath =>
        val accountsSelection = accountsSelectionFromPath match {
          case "joint" => AccountsSelection.joint
          case _ => AccountsSelection.joints
        }
        path("""\d{4}-\d{2}""".r) { yearMonth =>
          extractLog { log =>
            complete {
              Try(YearMonth.parse(yearMonth))
                .map { month =>
                  val operations = Operations.from(Events.events, accountsSelection).filter(o => at(month)(o.date))
                  val op1 = Operations.daily(Events.events, accountsSelection).filter(o => at(month)(o.date)) // TODO
                  val labels = Seq.tabulate(month.lengthOfMonth() - 1)(day => month.atDay(day + 1))
                  val cumulativeBalances: Seq[Amount] = BalanceEachDay.cumulativeBalances(Joints, month)
                  HttpResponse(OK, entity = HttpEntity(ContentTypes.`text/html(UTF-8)`, html.monthly(month, operations, labels, cumulativeBalances).toString))
                }
                .fold(error => {
                  log.error(error, "error")
                  HttpResponse(BadRequest)
                }, a => a)
            }
          }
        }
      } ~
      path(Remaining) { remainingPath =>
        get {
          val file = Paths.get(remainingPath)
          if (file.toFile.exists()) {
            complete(HttpResponse(OK, entity = HttpEntity(ContentTypes.`text/html(UTF-8)`, Files.readAllBytes(file))))
          } else {
            complete(NotFound)
          }
        }
      }

  startServer("localhost", 8080)

}
