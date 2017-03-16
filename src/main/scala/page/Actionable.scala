package github.samblake.scalatest.page

import org.openqa.selenium.WebDriver

/**
  * Created by sam on 3/16/17.
  */
/**
  * A trait that represents actions to be performed against a web page. Provides methods
  * that take thunk containing the actions to be performed on the page. Should be extended
  * to provide the full path to the page as well as methods that contain encapsulated,
  * reusable logic.
  *
  * @tparam T The F-bounded type (T should be the class that extends WebPage)
  */
trait Actionable[T <: Actionable[T]] {

  /**
    * The expected URL the action will be performed against.
    * @return The URL prefix
    */
  def url:String

  /**
    * Performs the supplied actions against the page.
    * @param actions The actions to perform
    * @tparam P The returned [[WebPage]]
    * @return The WebPage that the browser will display after the actions have been performed
    */
  def apply[P <: WebPage[P]](actions: (T) => P)(implicit webDriver: WebDriver):P

  /**
    * Performs the supplied actions against the page.
    * @param actions The actions to perform
    * @tparam P The returned [[WebPage]]
    * @return The WebPage that the browser will display after the actions have been performed
    */
  def and[P <: WebPage[P]](actions: T => P)(implicit webDriver: WebDriver):P

  /**
    * Performs the supplied actions against the page. Unlike the other methods that take actions
    * this one doesn't return a page. It is intended to be used by the final page in the chain,
    * therefore there will be no subsequent page to navigate to.
    * @param actions The actions to perform
    */
  def lastly[P <: WebPage[P]](actions: T => Unit)(implicit webDriver: WebDriver):Unit
}
