package github.samblake.scalatest.page

import org.scalatest.Matchers
import org.scalatest.selenium.{Page, WebBrowser}
import WebPage.BaseUrl
import org.openqa.selenium.WebDriver
import org.scalatest.concurrent.Eventually

/**
  * The base page class. The expected URL will be automatically checked against the actual URL
  * unless [[unchecked]] is called.
  *
  * @param baseUrl The base URL of the site under test
  * @tparam T The F-bounded type (T should be the class that extends WebPage)
  */
abstract class WebPage[T <: WebPage[T]](implicit baseUrl: BaseUrl) extends Page
      with Actionable[T] with Matchers with WebBrowser with Eventually {
  this:T =>

  /**
    * The URL path that should come after the base URL. This is used to supply to location of
    * the page if [[PageNavigation.go]] is called or, for validation if any of the [[Actionable]]
    * methods are called.
    * @return The path
    */
  def path:String

  override val url:String = baseUrl + "/" + path

  override def apply[P <: WebPage[P]](actions: (T) => P)(implicit webDriver: WebDriver):P = and(actions)

  override def and[P <: WebPage[P]](actions: T => P)(implicit webDriver: WebDriver):P = actions(this)

  override def lastly[P <: WebPage[P]](actions: T => Unit)(implicit webDriver: WebDriver):Unit = actions(this)

  /**
    * Returns a [[ValidatingPage]] wrapping _this_ that performs no automatic  URL validation.
    * @return A page that will have no automatic validation is performed against it
    */
  def unchecked = new NonValidatingPage[T](this)
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