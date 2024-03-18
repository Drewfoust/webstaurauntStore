import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import java.util.ArrayList;
import java.util.List;



public class WebstaurantStoreAutomationTest {

    public static void main(String[] args) throws InterruptedException {

        //Setup DB connection to retrieve data to assert against.
        /*
        String url = "jdbc:mysql://localhost:3306/mydatabase";
        String username = "sheeb.wooley";
        String password = "OneEyeOneHornFlyingPurplePeopleEater123";
        int expectedProductCount = 0;

        try (Connection connection = DriverManager.getConnection(url, username, password);
             Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery("SELECT COUNT(*) AS count FROM products WHERE description LIKE '%stainless work table%'");
            if (resultSet.next()) {
                expectedProductCount = resultSet.getInt("count");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }
        */
        int expectedProductCount = 540; // statically set value for assertion


        System.setProperty("webdriver.chrome.driver", "C:\\Users\\digit\\Downloads\\Drew\\WebDrivers\\chromedriver_win64\\chromedriver_122.exe");
        WebDriver driver = new ChromeDriver();
        driver.get("https://www.webstaurantstore.com/");
        WebElement searchBox = driver.findElement(By.name("searchval"));
        searchBox.sendKeys("stainless work table");
        searchBox.submit();

        WebDriverWait wait = new WebDriverWait(driver, 5);
        ArrayList<Object> searchResults = new ArrayList<>();
        ArrayList<Object> noTable = new ArrayList<>();

        int pageNumber = 1;

        List<WebElement> pElements;
        while (true) {
            pElements = driver.findElements(By.cssSelector("#ProductBoxContainer > div.group > a > span"));
            for (WebElement pElement : pElements) {
                String productText = pElement.getText();
                if (productText.toLowerCase().contains("table")) {
                    searchResults.add(productText);
                } else {
                    noTable.add(productText);
                }
            }

            List<WebElement> nextPageBtns = driver.findElements(By.cssSelector("a[aria-label='go to page " + (pageNumber + 1) + "']"));
            if (nextPageBtns.isEmpty()) {
                break;
            } else {
                WebElement nextPageBtn = nextPageBtns.get(0);
                nextPageBtn.click();
                wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("a[data-testid='cart-button']")));
                pageNumber++;
            }

        }

        Object lastRecord = searchResults.get(searchResults.size() - 1);

        int totalCount = searchResults.size();
        if (!noTable.isEmpty()){
            System.out.println("These are the products missing 'Table' from their description \n" + noTable);
        }

        assert (totalCount + 1) == expectedProductCount : "The total count returned from search does not equal expected count returned from DB";
        //System.out.println("\nTotal count of products returned: " + (totalCount + 1));
        System.out.println("\nLast product returned from 'stainless work table' search: \n" + lastRecord);

        String a = "//span[text()='"+lastRecord+"']";
        WebElement productElement = driver.findElement(By.xpath(a));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", productElement);
        Thread.sleep(3000);

        //Page details were too vague for me to find the 'addToCartButton' for the specific productElement
        //WebElement addToCartButton = productElement.findElement(By.cssSelector("#ProductBoxContainer > div.add-to-cart > form > div.btn-container > div.cartAndQuality > input.submit"));

        //This works, but it is statically set. Could enhance the script to pick a random number within searchResults.size() and selecting that random productElement and this static
        //value would be useless.
        //WebElement addToCartButton = productElement.findElement(By.xpath("/html/body/div[2]/div/div[4]/div[1]/div[3]/div[60]/div[4]/form/div/div/input[2]"));

        //This xpath works but finds the first AddToCartButton not the specific one.
        //WebElement addToCartButton = productElement.findElement(By.xpath("//*[@id=\"ProductBoxContainer\"]/div[4]/form/div/div/input[2]"));

        productElement.click();
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("buyButton")));
        WebElement addToCart = driver.findElement(By.id("buyButton"));
        addToCart.click();
        Thread.sleep(1000);
        WebElement openCart = driver.findElement(By.cssSelector("a[data-testid='cart-button']"));
        openCart.click();
        Thread.sleep(1000);
        WebElement emptyCart = driver.findElement(By.cssSelector("#main > div.cart-recommended > div > div.cartItemsHeader.toolbar.clears > div > button"));
        emptyCart.click();
        Thread.sleep(1000);
        WebElement confirmEmptyCart = driver.findElement(By.xpath("//*[@id=\"td\"]/div[11]/div/div/div/footer/button[1]"));
        confirmEmptyCart.click();

        driver.quit();

    }

}