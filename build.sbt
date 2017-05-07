name := "scalapages"
version := "0.1"
scalaVersion := "2.11.8"

libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.3"
libraryDependencies += "org.seleniumhq.selenium" % "selenium-java" % "2.35.0"
libraryDependencies += "com.typesafe" % "config" % "1.3.1"

logBuffered in Test := false
parallelExecution in Test := false

lazy val setChromeDriver = taskKey[Unit]("setChromeDriver")
setChromeDriver := System.setProperty("webdriver.chrome.driver", "bin/"+chromeDriver)

def chromeDriver = sys.props("os.name").toLowerCase match {
  case os if os.startsWith("win") => "chromedriver.exe"
  case os if os.startsWith("mac") => "chromedriver.mac"
  case _                          => "chromedriver"
}

(test in Test) <<= (test in Test) dependsOn setChromeDriver