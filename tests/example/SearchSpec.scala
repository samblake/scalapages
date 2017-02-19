package github.samblake.scalatest.page.example

import github.samblake.scalatest.page._

import scala.concurrent.duration._

/**
  * The example from http://www.scalatest.org/user_guide/using_selenium reworked.
  */
class SearchSpec extends PageSpec {

  val google = new Google()

  "After searching the results page" should "have the correct title" in {
    go to google and { home =>
      home search "Cheese!"
    } lastly { results =>
      results checkTitle
    }
  }

  class Google extends WebPage[Google] {
    override def path = "webhp"

    val searchField = "q"

    def search(term: String) = {
      click on searchField
      textField(searchField).value = term
      submit()
      new Results(term)
    }
  }

  class Results(term: String) extends WebPage[Results] {
    override def path = "search"

    def checkTitle() = eventually (timeout(3 seconds)) {
      pageTitle should be (term + " - Google Search")
    }
  }
}
