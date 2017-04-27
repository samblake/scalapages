package page

import github.samblake.scalatest.page.{Failable, WebPage}
import github.samblake.scalatest.page.WebPage.{FailingPage, UnvalidatedPage, ValidatedPage, ValidatingPage}
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
      driver get page.url
      page validate
    }
  }

  implicit def webPage2ValidatingPage[T <: WebPage[T]](webPage: T):ValidatingPage[T] = new ValidatedPage[T](webPage)
  implicit def failable2ValidatingPage[T <: WebPage[T]](failable: Failable[T,_]):ValidatingPage[T] = failable.success

  def unvalidated[T <: WebPage[T]](webPage: T):ValidatingPage[T] = new UnvalidatedPage[T](webPage)
  def failing[T <: WebPage[T]](webPage: T):ValidatingPage[T] = new FailingPage[T](webPage)

}