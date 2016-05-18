package com.opalab.protox6

import akka.actor.{Props, ActorSystem, Actor, ActorLogging}


trait Site extends Actor with ActorLogging {
  def find(query: String) {}
}

trait XMLResponse extends Site {

}

trait HTMLResponse extends Site {

}

class ModelsCom extends Site with XMLResponse {
  def receive = {
    case msg => log.info(s"Got ${msg}")
  }
}

class OutCom extends Site with HTMLResponse {
  def receive = {
    case msg => log.info(s"Got ${msg}")
  }
}

object SearchMachine {

  case class Find(query: Option[String] = None)

  val Sites = List(classOf[ModelsCom], classOf[OutCom])

  def props(): Props = Props(new SearchMachine(None))
}

case class SearchMachine(query: Option[String] = None) extends Actor with ActorLogging {
  def receive = {
    case msg => log.info(s"Got unhandled ${msg}")
  }
}


object SearchApp extends App {
  println("This is SearchApp \\m/")
  var system = ActorSystem.create(name = "SearchApp")


  val searchMachine = system.actorOf(SearchMachine.props())

  searchMachine ! SearchMachine.Find(query = Some("Tom Ford"))

}
