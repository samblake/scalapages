package github.samblake.scalatest.page.example

import github.samblake.scalatest.page.WebPage
import github.samblake.scalatest.page.WebPage.BaseUrl
import org.openqa.selenium.WebDriver
import org.scalatest.concurrent.Eventually
import org.scalatest.selenium.WebBrowser

import scala.concurrent.duration._

class GooglePages(implicit baseUrl: BaseUrl, webDriver: WebDriver) extends WebBrowser {

  def home() = new Home()
  def results(term: String) = new Results(term)

  class Home extends WebPage[Home] {
    override def path = "webhp"

    val searchField = "q"

    def search(term: String) = {
      click on searchField
      textField(searchField).value = term
      submit()
      results(term)
    }
  }

  class Results(term: String) extends WebPage[Results] with Eventually {
    override def path = "search"

    def checkTitle() = eventually (timeout(3 seconds)) {
      pageTitle should be (term + " - Google Search")
    }
  }
}