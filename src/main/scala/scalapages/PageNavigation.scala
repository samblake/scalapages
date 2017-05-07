package scalapages

import github.samblake.scalatest.page.WebPage.{BaseUrl, FailingPage, SimplePage, UnvalidatedPage, ValidatedPage, ValidatingPage}
import github.samblake.scalatest.page.{Failable, WebPage}
import org.openqa.selenium.WebDriver

/**
  * A trait that provides an enhanced [[org.scalatest.selenium.WebBrowser.go]] object.
  * It should be mixed into your [[org.scalatest.TestSuite]] ([[org.scalatest.FlatSpec]],
  * etc.).
  *
  * Provides methods that facilitate navigation and implicit methods to convert between
  * common types and their default [[ValidatingPage]].
  */
trait PageNavigation {

  /**
    * Returns the page that is passed into the [[PageNavigation go.to)]] method wrapped
    * in a [[ValidatingPage]] allowing the [[WebPage.and]] method to be called immediately
    * after.
    */
  object go {
    def to[T <: WebPage[T]](page: ValidatingPage[T])(implicit driver: WebDriver):T = {
      driver get page.url
      page validate
    }
  }

  /**
    * Adds a simple 'expect...' DSL to make explicitly returning pages a little more fluent.
    */
  object expect {
    def path(path: String)(implicit baseUrl: BaseUrl):SimplePage = baseUrl/path
    def page[T <: WebPage[T]](page: T):T = page
    def apply[T <: WebPage[T]](page: T):T = page
  }

  /** Return an unvalidated version of the [[WebPage]] */
  def unvalidated[T <: WebPage[T]](webPage: T)(implicit driver: WebDriver, baseUrl: BaseUrl):ValidatingPage[T] = new UnvalidatedPage[T](webPage)

  /** Return a version of the [[WebPage]] who's validation is expected to fail */
  def failing[T <: WebPage[T]](webPage: T)(implicit driver: WebDriver, baseUrl: BaseUrl):ValidatingPage[T] = new FailingPage[T](webPage)

  implicit def webPage2ValidatingPage[T <: WebPage[T]](webPage: T)
    (implicit driver: WebDriver, baseUrl: BaseUrl):ValidatingPage[T] = new ValidatedPage[T](webPage)

  implicit def failable2ValidatingPage[T <: WebPage[T]](failable: Failable[T,_])
    (implicit driver: WebDriver, baseUrl: BaseUrl):ValidatingPage[T] = failable.success
}