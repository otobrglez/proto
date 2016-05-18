package com.opalab.protox4

import akka.http.scaladsl.Http
import akka.actor.ActorSystem
import akka.http.scaladsl.model.ws.Message
import akka.http.scaladsl.server.Directives
import akka.stream.ActorMaterializer
import akka.stream.Materializer
import akka.stream.scaladsl.{Sink, Flow}
import com.typesafe.config.ConfigFactory

import scala.util.{Success, Failure}

/*
*
* https://github.com/ScalaConsultants/wsug-akka-websockets/blob/master/src/main/scala/io/scalac/wsakka/chat/ChatRoom.scala
* https://gist.github.com/josdirksen/3d4545a68137ae97f49b
* */

class Webservice(implicit fm: Materializer, system: ActorSystem) extends Directives {

  def route =
    pathSingleSlash {
      complete("Hey there.")
    } ~
      path("s") {
        parameter('name) { name =>
          // handleWebSocketMessages(webSocketFlow(sender = name))
          complete(s"Hey ${name}")
        }
      }

  /*
  def webSocketFlow(sender: String): Flow[Message, Message, Any] =
    Flow[Message].collect {
      case x => x
    }.via
    */
}

trait Configuration {
  final val config = ConfigFactory.load()
}

object Boot extends App with Configuration {
  implicit val system = ActorSystem.create(name = "ProtoX4")

  import system.dispatcher

  implicit val materializer = ActorMaterializer()

  var service = new Webservice

  var serverSource = Http().bind(
    interface = config.getString("ProtoX4.app.interface"),
    port = config.getInt("ProtoX4.app.port")
  )

  var binding = serverSource.to(Sink.foreach {
    connection =>
      println(s"Connection: ${connection.remoteAddress.getAddress}")
      connection.handleWith(service.route)
  }).run()

  binding.onComplete {
    case Success(s) => {
      println(s"Successfully bound to ${s.localAddress.getAddress} ${s.localAddress.getPort}")
    }
    case Failure(e) => {
      println(e.getMessage)
    }
  }
}
