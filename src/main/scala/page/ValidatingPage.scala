package github.samblake.scalatest.page

import org.openqa.selenium.WebDriver
import org.scalatest.Matchers

/**
  * A wrapper around a [[WebPage]] that performs automatic validation of the expected URL.
  *
  * @param page The [[WebPage]] to wrap
  * @tparam T The F-bounded type (T should be the class that extends WebPage)
  */
abstract class ValidatingPage[T <: WebPage[T]](val page: T) extends Actionable[T] with Matchers {

  override def url:String = page.url

  /**
    *
    * @param webDriver
    */
  def validate()(implicit webDriver: WebDriver):Unit

  override def apply[P <: WebPage[P]](actions: (T) => P)(implicit webDriver: WebDriver): P = and(actions)

  override def and[P <: WebPage[P]](actions: (T) => P)(implicit webDriver: WebDriver): P = {
    validate()
    page.and(actions)
  }

  override def lastly[P <: WebPage[P]](actions: (T) => Unit)(implicit webDriver: WebDriver): Unit = {
    validate()
    page.lastly(actions)
  }
}

/**
  * A validator that checks the current URL starts with the expected URL.
  *
  * @param page The [[WebPage]] to wrap
  * @tparam T The F-bounded type (T should be the class that extends WebPage)
  */
class UrlValidatingPage[T <: WebPage[T]](page: T) extends ValidatingPage(page) {
  override def validate()(implicit webDriver: WebDriver):Unit = page.currentUrl should startWith (url)
}

/**
  * A validator that performs no validation on the current URL.
  *
  * @param page The [[WebPage]] to wrap
  * @tparam T The F-bounded type (T should be the class that extends WebPage)
  */
class NonValidatingPage[T <: WebPage[T]](page: T) extends ValidatingPage(page) {
  override def validate()(implicit webDriver: WebDriver):Unit = Unit
}

