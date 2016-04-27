import java.util.UUID

import spray.json.DefaultJsonProtocol

case class Person(
                   first_name: String,
                   last_name: Option[String] = None,
                   uuid: Option[String] = Some(UUID.randomUUID().toString)
                 ) {

  override def toString = {
    (first_name + " " + last_name.getOrElse("") + " " + uuid.get).trim
  }
}

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import DefaultJsonProtocol._
import spray.json._

trait JsonSupportProtocols
  extends DefaultJsonProtocol
    with SprayJsonSupport {

  implicit val personFormat = jsonFormat3(Person.apply)
}

object Person extends JsonSupportProtocols {

}


val meh = Person("Oto").toJson
val x = Person.apply("Martina", Some("Herič")).toJson
// val y = Person("Janez","Novak", "555abbc7-0ca5-4892-975e-f3da8a44254b")


println(x.toJson)

