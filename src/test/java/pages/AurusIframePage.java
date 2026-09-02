package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class AurusIframePage {

    private WebDriver driver;
    private WebDriverWait wait;

    // 1. Locators stored cleanly at the top of the POM class
    private By cardNumberField = By.xpath("//*[@id='cNumber']");
    private By expiryDateField = By.xpath("//*[@id='exDate']");
    private By securityCodeField = By.xpath("//*[@id='secCode']");

    // Constructor initializing the driver instance context
    public AurusIframePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(20));
    }

    // 2. Encapsulated action methods
    public void setupTokenEventListener() {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript(
            "window.latestAurusToken = '';" +
            "window.addEventListener('message', function(event) {" +
            "    if (event.data) {" +
            "        let dataString = typeof event.data === 'object' ? JSON.stringify(event.data) : event.data.toString();" +
            "        if (dataString.includes('one_time_token')) {" +
            "            window.latestAurusToken = dataString;" +
            "        }" +
            "    }" +
            "});"
        );
    }

    public void enterCardDetails(String cardNumber, String expiryDate, String cvv) {
        WebElement cardNo = wait.until(ExpectedConditions.visibilityOfElementLocated(cardNumberField));
        
        // Clear card field safely via keyboard action chord shortcuts
        cardNo.sendKeys(org.openqa.selenium.Keys.chord(org.openqa.selenium.Keys.CONTROL, "a"));
        cardNo.sendKeys(org.openqa.selenium.Keys.BACK_SPACE);
        
        // Type characters slowly to let formatting scripts parse them safely
        for (char digit : cardNumber.toCharArray()) {
            cardNo.sendKeys(String.valueOf(digit));
            try { Thread.sleep(50); } catch (InterruptedException ignored) {}
        }

        // Handle Expiry Field cleanly
        WebElement exDate = driver.findElement(expiryDateField);
        exDate.sendKeys(org.openqa.selenium.Keys.chord(org.openqa.selenium.Keys.CONTROL, "a"));
        exDate.sendKeys(org.openqa.selenium.Keys.BACK_SPACE);
        exDate.sendKeys(expiryDate);
        
        // Handle CVV Field cleanly
        WebElement secCode = driver.findElement(securityCodeField);
        secCode.sendKeys(org.openqa.selenium.Keys.chord(org.openqa.selenium.Keys.CONTROL, "a"));
        secCode.sendKeys(org.openqa.selenium.Keys.BACK_SPACE);
        secCode.sendKeys(cvv);
    }

    public void triggerTokenGeneration() {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("getCardToken();");
    }

    public String pollForGeneratedToken() {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        for (int i = 0; i < 30; i++) { 
            try { Thread.sleep(500); } catch (InterruptedException ignored) {}
            Object tokenObj = js.executeScript("return window.latestAurusToken;");
            if (tokenObj != null && !tokenObj.toString().isEmpty()) {
                return tokenObj.toString();
            }
        }
        return "";
    }
}