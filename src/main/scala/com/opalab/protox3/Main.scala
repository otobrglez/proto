package com.opalab.protox3

import akka.actor._
import com.typesafe.config.ConfigFactory
import scala.concurrent.duration._
import akka.routing._

class Master extends Actor with ActorLogging {

  var router = {
    val routees = Vector.fill(2) {
      val r = context.actorOf(Props[Collector])
      context watch r
      ActorRefRoutee(r)
    }
    Router(RoundRobinRoutingLogic(), routees)
  }

  def receive = {
    case collect: CollectorProtocol.Collect => {
      router.route(collect, self)
    }
    case Terminated(actor) => {
      router = router.removeRoutee(actor)
      val r = context.actorOf(Props[Collector])
      context watch r
      router = router.addRoutee(r)
      log.info("Added routee...")
    }

    case msg => log.info(s"Got ${msg}")
  }
}

class Collector extends Actor with ActorLogging {
  def onStart = {
    log.info(s"Started new,...")
  }

  def receive = {
    case msg => {
      log.info(s"Got ${msg}")
      // self ! PoisonPill
    }
  }
}

object CollectorProtocol {

  case class Collect(url: String)

}


object Main extends App {

  import scala.concurrent.ExecutionContext.Implicits.global

  var system = ActorSystem.create(name = "Protox3", config = ConfigFactory.load().getConfig("Protox3"))

  /*
  // var master = system.actorOf(Props[Master].withRouter(RandomPool(10)),name="Master")
  var master = system.actorOf(Props[Master].withRouter(FromConfig()), name = "Master")
  master ! CollectorProtocol.Collect(url = "http://opalab.com")
  master ! CollectorProtocol.Collect(url = "http://otobrglez.opalab.com")
  master ! CollectorProtocol.Collect(url = "https://www.youtube.com/watch?v=IcrbM1l_BoI")

  system.scheduler.schedule(1 second, 1 second, master, CollectorProtocol.Collect(url = "http://delo.si"))
    */

  var collectorRef = system.actorOf(Props[Collector].withRouter(FromConfig()), name = "Collector")

  system.scheduler.schedule(1 second, 1000 milliseconds, collectorRef, CollectorProtocol.Collect(url = "http://delo.si"))


}
