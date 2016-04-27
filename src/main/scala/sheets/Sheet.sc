import scala.collection.immutable.SortedMap
import java.util.UUID

case class Person(
                   first_name: String,
                   last_name: Option[String] = None
                 ) {
  var uuid: UUID = UUID.randomUUID()

  def asTuple = uuid -> this

  override def toString = {
    (first_name + " " + last_name.getOrElse("")).trim
  }
}

var people = SortedMap.empty[UUID, Person]

people += Person("Jaka", Some("Novak")).asTuple
people += Person("Oto").asTuple
people += Person("Martina").asTuple

for (pair <- people) {
  println(s"${pair._2} ${pair._1}")
}


val key = "Oto"

val meh = people.find((pair) => pair._2.first_name == key).get

println(meh)

println(people(meh._1))
