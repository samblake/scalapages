# ScalaPages #

A page object DSL for [ScalaTest](http://www.scalatest.org/) and [Selenium](http://www.scalatest.org/user_guide/using_selenium).

Page specific logic is encapsulated in page specific classes. Actions to be performed on a page are scoped to blocks.

```scala
  "After searching, the results page" should "have the correct title" in {
    go to home and { > =>
      > search "Cheese!"
    } lastly { > =>
      > check title
    }
  } 
```

### How To Run ###

Clone the repo and run:

```
./gradlew test
```

### To Do ###

Scope Injection: boilerplate-free scope propagation à la [DSL Paradise](https://github.com/dsl-paradise/dsl-paradise).
