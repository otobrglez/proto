package com.opalab.proto.models

import java.util.UUID

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.DefaultJsonProtocol
import spray.json._

trait JsonSupportProtocols extends DefaultJsonProtocol with SprayJsonSupport {
  implicit val personProtocol = jsonFormat3(Person.apply)
}

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

object Person extends JsonSupportProtocols
