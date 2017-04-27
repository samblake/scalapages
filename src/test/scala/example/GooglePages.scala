package github.samblake.scalatest.page.example

import github.samblake.scalatest.page.WebPage
import github.samblake.scalatest.page.WebPage.BaseUrl
import org.openqa.selenium.WebDriver
import org.scalatest.Assertion
import org.scalatest.selenium.WebBrowser

import scala.concurrent.duration._

/**
  * A wrapper around the [[WebPage]]s to be used by the tests.
  *
  * @param baseUrl The base URL of the site under test
  * @param webDriver The webDriver to be used by Selenium
  */
class GooglePages(implicit baseUrl: BaseUrl, webDriver: WebDriver) extends WebBrowser {

  // Methods to provide access to the pages
  def home:Home = new Home()
  def results(term: String) = new Results(term)

  /** The home page. */
  class Home extends WebPage[Home] with ForwardingPage[Home] {
    override def path = "webhp"

    val searchField = "q"

    /**
      * Performs a search. As this method will navigate to another page it returns that page.
      * This allows chaining of the actions that will be performed on the subsequent page.
      */
    def search(term: String): Results = {
      click on searchField
      textField(searchField).value = term
      submit()

      // propagate next page
      results(term)
    }
  }

  /**
    * The search results page.
    * @param term The text entered into the search
    */
  class Results(term: String) extends WebPage[Results] with ForwardingPage[Results] {
    override def path = "webhp"

    /** Validates the page title is as expected. */
    def check(checkable: Checkable): Assertion = eventually (timeout(3 seconds)) {
      pageTitle should be (term + " - Google Search")
    }
  }

  sealed trait Checkable
  object title extends Checkable

  trait ForwardingPage[T <: WebPage[T]] {
    self: WebPage[T] =>

    // May be forwarded to local version i.e. google.co.uk
    override def check()(implicit webDriver: WebDriver): Unit = currentUrl should include(path)
  }
}