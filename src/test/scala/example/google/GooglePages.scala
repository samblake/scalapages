package example.google

import github.samblake.scalatest.page.WebPage
import github.samblake.scalatest.page.WebPage.BaseUrl
import org.openqa.selenium.WebDriver
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
  def home() = new Home()
  def results(term: String) = new Results(term)

  /**
    * The home page.
    */
  class Home extends WebPage[Home] {
    override def path = "webhp"

    val searchField = "q"

    /**
      * Performs a search. As this method will navigate to another page it returns that page.
      * This allows chaining of the actions that will be performed on the subsequent page.
      */
    object search {
      def using(term: String):Results = {
        click on searchField
        textField(searchField).value = term
        submit()
        results(term)
      }
    }
  }

  /**
    * The search results page.
    * @param term The text entered into the search
    */
  class Results(term: String) extends WebPage[Results] {
    override def path = "search"

    /**
      * Validates the page title is as expected.
      */
    object check {
      def title = eventually (timeout(3 seconds)) {
        pageTitle should be (term + " - Google Search")
      }
    }
  }
}