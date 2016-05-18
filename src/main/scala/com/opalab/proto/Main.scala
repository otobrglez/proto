package com.opalab.proto

import _root_.com.typesafe.config.ConfigFactory
import akka.actor._
import akka.event.Logging
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import com.opalab.proto.services.ProtoService

import scala.io.StdIn
import scala.util.{Failure, Success}

trait Configuration {
  final val config = ConfigFactory.load()
}

object PingPongProtocol {

  case object Ping

  case object Pong

}


class MasterPong extends Actor {
  val log = Logging(context.system, this)
  val littleChild = context.system.actorOf(Props[OtherActor], name = "other-actor")


  override def preStart : Unit = {
    log.info("Pre start...")
  }

  def receive = {
    case msg => {
      log.info(s"MasterPong / msg = ${msg}")
      littleChild ! "sent from MasterPong"
    }
  }
}

class OtherActor extends Actor with ActorLogging {
  def receive = {
    case msg: String => {
      log.info(s"OtherActor / msg = ${msg}")
    }
  }
}

class PongActor(master: ActorRef) extends Actor with ActorLogging {
  def receive = {
    case msg: String => {
      log.info(s"PongActor / msg = ${msg}")
      master ! "sent from PongActor"
    }
  }
}


object Main extends App with ProtoService with Configuration {
  override implicit val system = ActorSystem.create("proto")
  override implicit val executor = system.dispatcher
  override implicit val materializer = ActorMaterializer()


  implicit val masterPong = system.actorOf(Props[MasterPong], name = "pong-master")
  override implicit val pongActor = system.actorOf(Props(new PongActor(masterPong)), name = "pong")


  val bindingFuture = Http().bindAndHandle(
    routes, config.getString("http.interface"), config.getInt("http.port")
  )

  bindingFuture.onComplete {
    case Success(x) =>
      println(s"Server is listening on ${x.localAddress.getHostName}:${x.localAddress.getPort}")
    case Failure(e) =>
      println(s"Binding failed with ${e.getMessage}")
  }

  StdIn.readLine()
  bindingFuture.flatMap(_.unbind()).onComplete(_ => system.terminate())
}
