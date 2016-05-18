import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.dsl.DSL.Parse._
import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import net.ruippeixotog.scalascraper.model.Element

val siteUrl = "http://www.gq-magazine.co.uk/search?q=tom+ford"

case class Result(
                   label: String,
                   more_url: String,
                   extract: Option[String] = None,
                   images: Option[List[String]] = Some(List.empty[String]),
                   categories: Option[List[String]] = Some(List.empty[String])
                 ) {

  override def toString = label + " (" + more_url + ")"
}

var b = JsoupBrowser()
var doc = b.get(url = siteUrl)

doc.location

val cards: Option[List[Element]] = doc >?> elementList("article.c-card")

var siteHost : String = doc.location.substring(0, 7 + doc.location.replace("http://","").indexOf("/"))

cards.getOrElse {
  List.empty[Element]
}.map { article =>
  val headLinks: Option[List[Element]] = article >?> elementList("a.c-card__link--article")
  val headLink: Element = headLinks.getOrElse(List.empty[Element]).head

  Result(
    label = headLink.text,
    more_url = siteHost + headLink.attr("href")
  )
}
for (card <- cards) {
  println(card)
  println("")
}
