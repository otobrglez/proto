package com.opalab.proto.services

import akka.actor._
import akka.http.scaladsl.server.Directives._
import akka.stream.Materializer
import com.opalab.proto.PingPongProtocol
import com.opalab.proto.models._

import scala.collection.SortedMap
import scala.concurrent.{Future, ExecutionContextExecutor}

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.DefaultJsonProtocol
import spray.json._





trait ProtoService extends JsonSupportProtocols{
  implicit val system: ActorSystem

  implicit def executor: ExecutionContextExecutor

  implicit val materializer: Materializer

  // implicit val masterPong : ActorRef

  implicit val pongActor : ActorRef

  // pongActor ! PingPongProtocol.Ping



  var people = SortedMap.empty[String, Person]

  val routes = {
    pathSingleSlash {
      pongActor ! "hello"
      complete("proto")
    } ~
      pathPrefix("api" / "people") {
        path(JavaUUID) { id =>
          val getPerson = Future {
            people.getOrElse(id.toString, None)
          }

          onSuccess(getPerson) {
            case person: Person => complete(person)
            case _ => complete(404, None)
          }
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