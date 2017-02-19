package github.samblake.scalatest.page

import org.openqa.selenium.WebDriver
import org.scalatest._

abstract class PageSpec extends FlatSpec {

  object go {
    def to[P <: WebPage[P]](page: P)(implicit driver: WebDriver):P = {
      driver.get(page.url)
      page
    }
  }
}