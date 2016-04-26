package com.opalab

import akka.actor.{ActorSystem, _}
import scala.util.{Success, Failure}
import akka.event.Logging
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import scala.io.StdIn
import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory

class AkkaBot extends Actor {
  val log = Logging(context.system, this)
  // context.setReceiveTimeout(Duration.create("5 seconds"))

  def receive = {
    case message: String => {
      log.info(s"Got string ~> $message")
    }
    case _ => {
      log.info("Got something else then string,...")
    }
  }
}

object Main extends App {
  val config = ConfigFactory.load()

  implicit val system = ActorSystem.create("proto")
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher

  // val bot = system.actorOf(Props[AkkaBot], name = "simple-bot")
  // bot ! "oto was here"

  val route =
    get {
      pathSingleSlash {
        complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "<html><body>Something here. Proto</body></html>"))
      } ~
        path("ping") {
          complete("PONG!")
        } ~
        path("crash") {
          sys.error("BOOM!")
        }
    }

  val bindingFuture = Http().bindAndHandle(route, config.getString("http.interface"), config.getInt("http.port"))

  bindingFuture.onComplete {
    case Success(x) =>
      println(s"Server is listening on ${x.localAddress.getHostName}:${x.localAddress.getPort}")
    case Failure(e) =>
      println(s"Binding failed with ${e.getMessage}")
  }

  StdIn.readLine()
  bindingFuture.flatMap(_.unbind()).onComplete(_ => system.terminate())
}
