package com.opalab.protox2

import akka.actor._
import scala.concurrent.Future
import scala.concurrent.duration._
import scala.util.{Failure, Success}

object Protocol {

  case class ScrapeSite(url: String)

}

class Counter extends Actor with ActorLogging {

  import scala.concurrent.ExecutionContext.Implicits.global

  var count: Int = 0

  def incrementCounter: Future[Int] = Future {
    count += 1
    count
  }

  def receive = {
    case Counter.Stats => {
      if (count % 2 == 0) log.info(s"Count is ${count}")
      if (count == 20) self ! PoisonPill
    }
    case _ => {
      incrementCounter.onComplete {
        case Success(x: Int) => {
          log.info(s"Increased counter to ${x}")
          self ! Counter.Stats
        }
      }
    }
  }
}

object Counter {

  case class Stats()

}

class Scraper extends Actor with ActorLogging {
  def receive = {
    case Protocol.ScrapeSite(url) => log.info(s"Must scrape ${url}")
    case msg => log.error(s"Some unknown message. ${msg}")
  }
}

class Collector extends Actor with ActorLogging {
  def receive = {
    case msg => {
      log.info(s"Collector got ${msg}")
    }
  }
}

object Main extends App {

  import scala.concurrent.ExecutionContext.Implicits.global

  var system = ActorSystem.create(name = "protox2")
  val counter = system.actorOf(Props[Counter], name = "counter")
  val scraper = system.actorOf(Props[Scraper], name = "scraper")

  system.scheduler.schedule(1 second, 1 second, counter, None)

  scraper ! Protocol.ScrapeSite(url = "http://opalab.com")
  scraper ! Protocol.ScrapeSite(url = "http://otobrglez.opalab.com")
  scraper ! Protocol.ScrapeSite(url = "http://cnn.com")
  scraper ! "ddd"

  // Thread.sleep(5000)
  // system.terminate()
}
