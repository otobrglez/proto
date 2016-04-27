package com.opalab.proto

import _root_.com.typesafe.config.ConfigFactory
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import com.opalab.proto.services.ProtoService

import scala.io.StdIn
import scala.util.{Failure, Success}

trait Configuration {
  final val config = ConfigFactory.load()
}

object Main extends App with ProtoService with Configuration {
  override implicit val system = ActorSystem.create("proto")
  override implicit val executor = system.dispatcher
  override implicit val materializer = ActorMaterializer()

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
