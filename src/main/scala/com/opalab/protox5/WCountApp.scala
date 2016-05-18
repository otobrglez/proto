package com.opalab.protox5

import akka.actor.Status.Success
import akka.actor._
import akka.routing.RoundRobinPool

import scala.concurrent.Await
import scala.io.Source
import scala.util.matching.Regex
import akka.util.Timeout
import scala.concurrent.duration._
import akka.pattern.ask
import akka.dispatch._
import akka.dispatch.ExecutionContexts._


object Protocol {

  case class StartCountingIn(fileName: String)

  case class CounInLine(line: String)

  case class Counted(number: Int)

  case class CountCompleted(number: Int)

}

import Protocol._


class LinesInFileActor extends Actor with ActorLogging {
  var count = 0
  var totalLines = 0

  private var originalSender: Option[ActorRef] = None

  def receive = {
    case Protocol.StartCountingIn(name: String) => {
      originalSender = Some(sender)
      Source.fromFile(name).getLines.foreach { line =>
        context.actorOf(Props[WordCounter]) ! CounInLine(line)
        totalLines += 1
      }
    }
    case Counted(number: Int) => {
      count += number
      totalLines -= 1

      if (totalLines <= 0) {
        log.info("Count completed,... sending event.")
        originalSender.map(_ ! CountCompleted(count))
      }
    }
    case msg => log.info(s"LinesInFileActor received ${msg}")
  }
}

class WordCounter extends Actor with ActorLogging {
  def receive = {
    case CounInLine(line: String) => {
      sender ! Counted("[,!? ]+".r.findAllIn(line).length)
    }
  }
}


object WCountApp extends App {

  import scala.concurrent.ExecutionContext.Implicits.global

  implicit val timeout = Timeout(25.seconds)

  var system = ActorSystem.create("WCountSystem")

  var a = system.actorOf(Props[LinesInFileActor].withRouter(RoundRobinPool(5)), name = "LinesInFile")

  a ! StartCountingIn("/Users/otobrglez/Projects/fsearch/proto/some_page.html")


  //TODO: This does not work as expected. Further investigation needed.
  // Compute results
  val future = a ? CountCompleted
  var result = Await.result(future, timeout.duration) // .asInstanceOf[Any]
  println(result)
}
