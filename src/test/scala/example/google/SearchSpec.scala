package example.google

import github.samblake.scalatest.page.{NonValidatingPage, UrlValidatingPage, ValidatingPage, WebPage}

/**
  * The example from http://www.scalatest.org/user_guide/using_selenium reworked.
  */
class SearchSpec extends GoogleSpec {
  import pages._

  "After searching the results page" should "have the correct title" in {
      go to home and { home =>
      import home._
      search using "Cheese!"
    } lastly { results =>
      import results._
      check title
    }
  }
}