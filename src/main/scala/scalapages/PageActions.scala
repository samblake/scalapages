package scalapages

import org.openqa.selenium.{ElementNotVisibleException, WebDriver, WebElement}
import org.scalactic.source
import org.scalatest.exceptions.{StackDepthException, TestFailedException}
import org.scalatest.selenium.WebBrowser

/**
  * Additional actions that can be mixed in to specs, not really related to page objects themselves.
  */
trait PageActions {

  /**
    * Adds a 'move to...' DSL similar to [[WebBrowser.clickOn]] except that will move the mouse pointer
    * to that location and won't perform a click. Useful when it's required to hover over a location for
    * menus etc. to pop up.
    */
  object move {
    def to(element: WebElement)(implicit driver: WebDriver): Unit =
      new org.openqa.selenium.interactions.Actions(driver).moveToElement(element).build().perform()
  }

  /**
    * Adds a 'click on buttonText ("Submit")' DSL similar to [[WebBrowser.linkText]] but for button
    * elements.
    */
  def buttonText(text: String)(implicit driver: WebDriver):WebElement = ButtonFinder.findButtons(text)

  /** Object to facilitate finding of buttons for [[buttonText]]. */
  private object ButtonFinder extends WebBrowser {
    def findButtons(text: String)(implicit driver: WebDriver, pos: source.Position = implicitly[source.Position]):WebElement = {
      val buttons = findAll(tagName("button")).filter(e => e.underlying.getText.trim.equals(text))
      if (buttons.isEmpty) {
        throw new TestFailedException(
          (_: StackDepthException) => Some("Button with text '" + text + "' not found."),
          Some(new ElementNotVisibleException("Button with text '" + text + "' not found.")),
          pos
        )
      }
      buttons.next.underlying
    }
  }
}