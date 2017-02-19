package github.samblake.scalatest.page

import org.scalatest.Matchers
import org.scalatest.selenium.{Page, WebBrowser}
import WebPage.BaseUrl

abstract class WebPage[T <: WebPage[T]](implicit baseUrl: BaseUrl) extends Page with Matchers with WebBrowser {
  this: T =>

  override val url: String = baseUrl + "/" + path
  def path: String

  def apply[P <: WebPage[P]](actions: (T) => P):P = actions(this)
  def and[P <: WebPage[P]](actions: T => P):P = actions(this)
  def lastly[P <: WebPage[P]](actions: T => Unit):Unit = actions(this)
}

object WebPage {

  def apply(baseUrl: BaseUrl, path: String): SimplePage = new SimplePage(path)(baseUrl)

  class SimplePage(p: String)(implicit baseUrl: BaseUrl) extends WebPage[SimplePage] {
    override def path = p
  }

  implicit class BaseUrl(baseUrl: String) {
    override def toString: String = baseUrl
    def apply(path: String): SimplePage = WebPage(this, path)
    def / (path: String): SimplePage = apply(path)
  }
}