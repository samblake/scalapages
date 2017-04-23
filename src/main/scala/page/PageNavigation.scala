package page

import github.samblake.scalatest.page.WebPage.{BaseUrl, ValidatedPage, ValidatingPage}
import github.samblake.scalatest.page.WebPage
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
    def to[T <: WebPage[T]](page: ValidatingPage[T])(implicit driver: WebDriver):T = {
      driver.get(page.url)
      page.validate
    }

    def to[T <: WebPage[T]](page: T)(implicit driver: WebDriver, baseUrl: BaseUrl):T = {
      to(new ValidatedPage[T](page))
    }
  }
}