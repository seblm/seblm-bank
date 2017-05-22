package name.lemerdy.sebastian.bank.infrastructure

import java.nio.file.{Files, Paths}
import java.time.LocalDate

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import akka.http.scaladsl.model.StatusCodes.{NotFound, OK}
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpResponse}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import name.lemerdy.sebastian.bank.Accounts.All
import name.lemerdy.sebastian.bank.balance.CumulativeBalance
import name.lemerdy.sebastian.bank._

import scala.concurrent.{ExecutionContext, Future}
import scala.io.StdIn

object WebServer extends App {

  implicit val system: ActorSystem = ActorSystem("seblm-bank")
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val exectutionContext: ExecutionContext = system.dispatcher

  private val chronologicalOrder: (((LocalDate, Any), (LocalDate, Any)) => Boolean) = {
    case ((d1, _), (d2, _)) => d1.isBefore(d2)
  }

  private def cumulativeBalance(account: Account): String = {
    Events.events
      .filter(event => if (account == All) !event.libelle.equals(Libelle("DEBIT CARTE BANCAIRE DIFFERE")) else account == event.account)
      .groupBy(_.date)
      .toSeq
      .sortWith(chronologicalOrder)
      .scanLeft(CumulativeBalance(Event(-1, account, LocalDate.parse("2016-03-26"), Amount(0L), Libelle("")), Amount(0L))) { case ((CumulativeBalance(_, cumulative)), (date, eventsSameDate)) =>
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

  val route: Route =
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

  val bindingFuture: Future[ServerBinding] = Http().bindAndHandle(route, "localhost", 8080)

  println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")
  StdIn.readLine()
  bindingFuture
    .flatMap(_.unbind())
    .onComplete(_ => system.terminate())

}
