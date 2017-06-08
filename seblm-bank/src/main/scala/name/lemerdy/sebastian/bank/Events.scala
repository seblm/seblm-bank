package name.lemerdy.sebastian.bank

import scala.io.{BufferedSource, Source}

object Events {

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
