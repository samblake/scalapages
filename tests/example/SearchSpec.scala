package github.samblake.scalatest.page.example

/**
  * The example from http://www.scalatest.org/user_guide/using_selenium reworked.
  */
class SearchSpec extends GoogleSpec {
  import pages._

  "After searching the results page" should "have the correct title" in {
    go to home and { home =>
      home search "Cheese!"
    } lastly { results =>
      results checkTitle
    }
  }
}
