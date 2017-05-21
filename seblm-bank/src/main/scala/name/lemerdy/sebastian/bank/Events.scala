package name.lemerdy.sebastian.bank

import scala.io.{BufferedSource, Source}

object Events {

  def events(f: Iterator[Event] => Unit): Unit = {
    val events = Source.fromFile("../events.tsv")
    f(events.getLines.flatMap(event => Event(event.split("\t"))))
    events.close
  }

  def events: Seq[Event] = {
    val bufferedSource: BufferedSource = Source.fromFile("../events.tsv")
    val events = bufferedSource.getLines()
      .mkString("\n")
      .split("\n")
      .flatMap(event => Event(event.split("\t")))
      .toSeq
    bufferedSource.close
    events
  }

}
