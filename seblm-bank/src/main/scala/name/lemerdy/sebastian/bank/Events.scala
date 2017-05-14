package name.lemerdy.sebastian.bank

import scala.io.Source

object Events {

  def events(f: Iterator[Event] => Unit): Unit = {
    val events = Source.fromFile("../events.tsv")
    f(events.getLines.flatMap(event => Event(event.split("\t"))))
    events.close
  }

}
