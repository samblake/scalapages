package scalapages.example

import scalapages.WebPage.BaseUrl
import org.openqa.selenium.WebDriver
import org.scalatest.FlatSpec
import org.scalatest.selenium.Chrome
import scalapages.PageNavigation

/**
  * A base spec supplying the implicit [[WebDriver]] and [[BaseUrl]], and the [[GooglePages]].
  * Mixes in the [[PageNavigation]] allowing specs to use the enhanced go object.
  */
class GoogleSpec extends FlatSpec with PageNavigation {

  implicit val driver: WebDriver = Chrome.webDriver
  implicit val baseUrl: BaseUrl = "http://www.google.com"

  val pages = new GooglePages()
}