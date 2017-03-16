# Advanced ScalaTest Pages #

Advanced page DSL for [ScalaTest](http://www.scalatest.org/) and [Selenium](http://www.scalatest.org/user_guide/using_selenium).

Page specific logic is encapsulated in Page classes. Actions to be performed on a page are scoped to blocks (lambdas).

```scala
  "After searching the results page" should "have the correct title" in {
    go to home and { home =>
      home search "Cheese!"
    } lastly { results =>
      results checkTitle
    }
  }
```
