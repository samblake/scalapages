package scalapages

import scalapages.WebPage.{BaseUrl, FailingPage, RedirectingPage, UnvalidatedPage, ValidatingPage}
import org.openqa.selenium.WebDriver
import org.scalatest.Matchers
import org.scalatest.concurrent.Eventually
import org.scalatest.exceptions.TestFailedException
import org.scalatest.selenium.{Page, WebBrowser}

/**
  * The base page class. Provides methods that take thunk containing the actions to be
  * performed on the page. Should be extended to provide the full path to the page as
  * well as methods that contain encapsulated, reusable logic.
  *
  * @param baseUrl The base URL of the site under test
  * @tparam T The F-bounded type (T should be the class that extends WebPage)
  */
abstract class WebPage[T <: WebPage[T]](implicit baseUrl: BaseUrl) extends Page
      with Matchers with WebBrowser with Eventually with Actionable[T] {
  self: T =>

  override val url: String = baseUrl + "/" + path
  def path: String

  /**
    * Performs the supplied actions against the page.
    * @param actions The actions to perform
    * @tparam P The returned [[WebPage]]
    * @return The WebPage that the browser will display after the actions have been performed
    */
  def apply[P <: WebPage[P]](actions: T => ValidatingPage[P])(implicit webDriver: WebDriver):ValidatingPage[P] = actions(this)

  /**
    * Performs the supplied actions against the page.
    * @param actions The actions to perform
    * @tparam P The returned [[WebPage]]
    * @return The WebPage that the browser will display after the actions have been performed
    */
  def and[P <: WebPage[P]](actions: T => ValidatingPage[P])(implicit webDriver: WebDriver):ValidatingPage[P] = actions(this)

  /**
    * Performs the supplied actions against the page. Unlike the other methods that take actions
    * this one doesn't return a page. It is intended to be used by the final page in the chain,
    * therefore there will be no subsequent page to navigate to.
    * @param actions The actions to perform
    */
  def lastly(actions: T => Unit):Unit = actions(this)

  /**
    * Performs simple validation that the page navigated to is that page that was expected. By default this
    * is done by checking that the URL in the browser starts with the URL defined for the page.
    *
    * This method is usually called by a [[ValidatingPage]].
    */
  protected def check()(implicit webDriver: WebDriver):Unit = currentUrl should startWith (url)

  /** Creates an [[UnvalidatedPage]] that performs no validation. **/
  def unchecked(implicit webDriver: WebDriver):UnvalidatedPage[T] = new UnvalidatedPage[T](this)

  /** Creates a [[FailingPage]] that expects validationperforms to fail. **/
  def failing(implicit webDriver: WebDriver):FailingPage[T] = new FailingPage[T](this)

  /** Navigates to the current [[WebPage]] but expects a redirect to the [[scalapages]] supplied. **/
  def redirectingTo[P <: WebPage[P]](page: P)(implicit webDriver: WebDriver):ValidatingPage[P] = new RedirectingPage(this, page)
}

/**
  * Provides implicit conversion from a [[String]] to a [[BaseUrl]].
  */
object WebPage {

  /**
    * Performs simple validation of a page after it has been navigated to.
    * @param webPage The page to validate
    * @param baseUrl The baseUrl of the page
    * @tparam T The type of the [[WebPage]]
    */
  abstract class ValidatingPage[T <: WebPage[T]](webPage: T)(implicit baseUrl: BaseUrl) extends Actionable[T] {
    def url: String = webPage.url

    /** Performs validation against the [[webPage]], usually by calling the [[webPage.check()]]. */
    protected def check(): Unit

    /**
      * Performs validation against the [[webPage]] and returns it.
      * @return
      */
    def validate: T = {
      check()
      webPage
    }

    override def apply[P <: WebPage[P]](actions: T => ValidatingPage[P])(implicit webDriver: WebDriver):ValidatingPage[P] = {
      check()
      webPage.apply(actions)
    }

    override def and[P <: WebPage[P]](actions: T => ValidatingPage[P])(implicit webDriver: WebDriver):ValidatingPage[P] = {
      check()
      webPage.and(actions)
    }

    override def lastly(actions: T => Unit):Unit = {
      check()
      webPage.lastly(actions)
    }
  }

  /** Performs the WebPages default validation. **/
  class ValidatedPage[T <: WebPage[T]](webPage: T)(implicit baseUrl: BaseUrl, webDriver: WebDriver) extends ValidatingPage(webPage) {
    override def check(): Unit = webPage.check()
  }

  /** Performs no validation. **/
  class FailingPage[T <: WebPage[T]](webPage: T)(implicit baseUrl: BaseUrl, webDriver: WebDriver) extends ValidatedPage(webPage) {
    override def check(): Unit = {
      try {
        super.check()
        assert(false, "Standard page check did not fail when expected")
      }
      catch {
        case _: TestFailedException => Unit
        case e: Exception           => assert(false, "Standard page check threw unexpected exception: " + e.getClass.getName)
      }
    }
  }

  /** Performs no validation. **/
  class UnvalidatedPage[T <: WebPage[T]](webPage: T)(implicit baseUrl: BaseUrl) extends ValidatingPage(webPage) {
    override def check(): Unit = Unit
  }

  /**
    * Navigates to the [[from]] page's URL but checks the [[to]] page's URL.
    * The [[to]] page is returned as the current page.
    */
  class RedirectingPage[F <: WebPage[F], T <: WebPage[T]](from: F, to: T)
     (implicit baseUrl: BaseUrl, webDriver: WebDriver) extends ValidatingPage[T](to) {
    override def url: String = from.url
    override def check(): Unit = to.check()
  }

  /**
    * Creates a page with no additional logic. Can be used for navigating to new URLs
    * without having to declare new classes.
    *
    * @param p The path of thr URL page after the base URL
    * @param baseUrl The base URL
    */
  class SimplePage(p: String)(implicit baseUrl: BaseUrl) extends WebPage[SimplePage] {
    override def path: String = p
  }

  /**
    * Models the base URL of the website being tested. Provides implicit conversion from
    * a [[String]]. Allows construction of pages with a full path via the [[apply]] and
    * [[/]] methods. For scalapages.example:
    *
    * {{{
    * "www.google.com"/"webhp"
    * }}}
    *
    * @param baseUrl The base URL
    */
  implicit class BaseUrl(baseUrl: String) {
    def apply(path: String): SimplePage = new SimplePage(path)(this)
    def / (path: String): SimplePage = apply(path)
    override def toString: String = baseUrl
  }
}

/**
  * A trait that allows actiuons to be performed againts a [[WebPage]].
  *
  * @tparam T The [[WebPage]] action will be performed against
  */
trait Actionable[T <: WebPage[T]] {

  /**
    * Performs the supplied actions against the page.
    * @param actions The actions to perform
    * @tparam P The returned [[WebPage]]
    * @return The WebPage that the browser will display after the actions have been performed
    */
  def apply[P <: WebPage[P]](actions: T => ValidatingPage[P])(implicit webDriver: WebDriver):ValidatingPage[P]

  /**
    * Performs the supplied actions against the page.
    * @param actions The actions to perform
    * @tparam P The returned [[WebPage]]
    * @return The WebPage that the browser will display after the actions have been performed
    */
  def and[P <: WebPage[P]](actions: T => ValidatingPage[P])(implicit webDriver: WebDriver):ValidatingPage[P]

  /**
    * Performs the supplied actions against the page. Unlike the other methods that take actions
    * this one doesn't return a page. It is intended to be used by the final page in the chain,
    * therefore there will be no subsequent page to navigate to.
    * @param actions The actions to perform
    */
  def lastly(actions: T => Unit):Unit
}

/**
  * Models the result of an action that results in the browser being forwarded to different pages
  * depending on whether the action was sucessful or not. For scalapages.example submission of a form that
  * returns the user to the originating page if validation fails.
  *
  * By default the Failable will be converted to the [[success]] [[WebPage]] by the implicit
  * [[scalapages.PageNavigation.failable2ValidatingPage]] function.
  *
  * @param success The page that will be displayed on success
  * @param failure The page that will be displayed on failure
  */
case class Failable[S <: WebPage[S], F <: WebPage[F]](success: S, failure: F)