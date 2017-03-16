package github.samblake.scalatest.page

import org.openqa.selenium.WebDriver

/**
  * A trait that provides an enhanced [[org.scalatest.selenium.WebBrowser.go]] object.
  * It should be mixed into your [[org.scalatest.TestSuite]] ([[org.scalatest.FlatSpec]],
  * etc.). It returns the page that is passed into the [[PageNavigation go.to)]] method,
  * wrapped in a [[ValidatingPage]] allowing the [[WebPage.and]] method to be called
  * immediately after.
  */
trait PageNavigation {

  object go {
    def to[P <: WebPage[P]](page: P)(implicit driver: WebDriver):ValidatingPage[P] = {
      to(new UrlValidatingPage[P](page))
    }

    def to[T <: WebPage[T]](page: ValidatingPage[T])(implicit driver: WebDriver):ValidatingPage[T] = {
      driver.get(page.url)
      page
    }
  }
}