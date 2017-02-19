package github.samblake.scalatest.page

import github.samblake.scalatest.page.WebPage.BaseUrl
import org.openqa.selenium
import org.openqa.selenium.{By, WebDriver}
import org.scalatest._
import org.scalatest.concurrent.Eventually
import org.scalatest.selenium.Chrome

/**
  * See http://www.scalatest.org/user_guide.
  */
abstract class PageSpec extends FlatSpec with Matchers
      with BeforeAndAfterAll with Eventually {

  implicit val driver: WebDriver = Chrome.webDriver

  implicit val baseUrl: BaseUrl = "http://www.google.com"

  override def beforeAll() = resize(768, 1024)

  override def afterAll() {
    driver.quit()
  }

  def pageText(implicit driver: WebDriver): String = driver.findElement(By.tagName("body")).getText

  implicit def toDimensions(dim: (Int, Int)): selenium.Dimension = new selenium.Dimension(dim._1, dim._2)

  private def resize(x: Int, y: Int)(implicit webDriver: WebDriver) = webDriver.manage.window.setSize(x, y)

  object go {
    def to[P <: WebPage[P]](page: P)(implicit driver: WebDriver):P = {
      driver.get(page.url)
      page
    }
  }
}