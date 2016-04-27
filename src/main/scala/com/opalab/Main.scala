package com.opalab

import java.util.UUID

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.server.Directives._
import akka.stream.{ActorMaterializer, Materializer}
import com.typesafe.config.ConfigFactory
import spray.json.{DefaultJsonProtocol, _}

import scala.collection.SortedMap
import scala.concurrent.ExecutionContextExecutor
import scala.io.StdIn
import scala.util.{Failure, Success}

case class Person(
                   first_name: String,
                   last_name: Option[String] = None,
                   var uuid: Option[String] = Some(UUID.randomUUID().toString)
                 ) {

  def asTuple = {
    this.uuid = Some(this.uuid.getOrElse(UUID.randomUUID().toString))
    this.uuid.get -> this
  }
}

trait JsonSupportProtocols extends DefaultJsonProtocol with SprayJsonSupport {
  implicit val personProtocol = jsonFormat3(Person.apply)
}

object Person extends JsonSupportProtocols

trait Configuration {
  final val config = ConfigFactory.load()
}

trait Service extends JsonSupportProtocols {
  implicit val system: ActorSystem

  implicit def executor: ExecutionContextExecutor

  implicit val materializer: Materializer

  var people = SortedMap.empty[String, Person]

  val routes = {
    pathSingleSlash {
      complete("proto")
    } ~
      pathPrefix("api" / "people") {
        path(JavaUUID) { id =>
          complete(people(id.toString))
        } ~
          logRequest("POST-PERSON") {
            post {
              entity(as[Person]) { person =>
                people += person.asTuple
                complete(person)
              }
            }
          } ~ complete(people.map((pair) => pair._2.toJson))
      }
  }
}


object Main extends App with Service with Configuration {
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
