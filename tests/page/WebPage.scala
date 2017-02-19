package github.samblake.scalatest.page

import org.scalatest.Matchers
import org.scalatest.selenium.{Page, WebBrowser}
import WebPage.BaseUrl
import org.scalatest.concurrent.Eventually

/**
  * The base page class. Provides methods that take thunk containing the actions to be
  * performed on the page. Should be extended to provide the full path to the page as
  * well as methods that contain encapsulated, reusable logic.
  *
  * @param baseUrl The base URL of the site under test
  * @tparam T The F-bounded type (T should be the class that extends WebPage)
  */
abstract class WebPage[T <: WebPage[T]](implicit baseUrl: BaseUrl) extends Page
      with Matchers with WebBrowser with Eventually {
  this: T =>

  override val url: String = baseUrl + "/" + path
  def path: String

  /**
    * Performs the supplied actions against the page.
    * @param actions The actions to perform
    * @tparam P The returned [[WebPage]]
    * @return The WebPage that the browser will display after the actions have been performed
    */
  def apply[P <: WebPage[P]](actions: (T) => P):P = actions(this)

  /**
    * Performs the supplied actions against the page.
    * @param actions The actions to perform
    * @tparam P The returned [[WebPage]]
    * @return The WebPage that the browser will display after the actions have been performed
    */
  def and[P <: WebPage[P]](actions: T => P):P = actions(this)

  /**
    * Performs the supplied actions against the page. Unlike the other methods that take actions
    * this one doesn't return a page. It is intended to be used by the final page in the chain,
    * therefore there will be no subsequent page to navigate to.
    * @param actions The actions to perform
    */
  def lastly[P <: WebPage[P]](actions: T => Unit):Unit = actions(this)
}

/**
  * Provides implicit conversion from a [[String]] to a [[BaseUrl]].
  */
object WebPage {

  def apply(baseUrl: BaseUrl, path: String): SimplePage = new SimplePage(path)(baseUrl)

  /**
    * Creates a page with no additional logic. Can be used for navigating to new URLs
    * without having to declare new classes.
    *
    * @param p The path of thr URL page after the base URL
    * @param baseUrl The base URL
    */
  class SimplePage(p: String)(implicit baseUrl: BaseUrl) extends WebPage[SimplePage] {
    override def path = p
  }

  /**
    * Models the base URL of the website being tested. Provides implicit conversion from
    * a [[String]]. Allows construction of pages with a full path via the [[apply]] and
    * [[/]] methods. For example:
    *
    * {{{
    * "www.google.com"/"webhp"
    * }}}
    *
    * @param baseUrl The base URL
    */
  implicit class BaseUrl(baseUrl: String) {
    def apply(path: String): SimplePage = WebPage(this, path)
    def / (path: String): SimplePage = apply(path)
    override def toString: String = baseUrl
  }
}