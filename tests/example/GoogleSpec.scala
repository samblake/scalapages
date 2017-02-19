package github.samblake.scalatest.page.example

import github.samblake.scalatest.page.PageSpec
import github.samblake.scalatest.page.WebPage.BaseUrl
import org.openqa.selenium
import org.openqa.selenium.WebDriver
import org.scalatest.BeforeAndAfterAll
import org.scalatest.selenium.Chrome

class GoogleSpec extends PageSpec with BeforeAndAfterAll {

  implicit val driver: WebDriver = Chrome.webDriver

  implicit val baseUrl: BaseUrl = "http://www.google.com"

  val pages = new GooglePages()

  override def beforeAll() = resize(768, 1024)

  override def afterAll() {
    driver.quit()
  }

  implicit def toDimensions(dim: (Int, Int)): selenium.Dimension = new selenium.Dimension(dim._1, dim._2)

  private def resize(x: Int, y: Int)(implicit webDriver: WebDriver) = webDriver.manage.window.setSize(x, y)
}
