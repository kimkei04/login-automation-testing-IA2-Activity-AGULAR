package tests;

import io.appium.java_client.AppiumBy;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class LoginTest extends BaseTest {

    private static final String PACKAGE = "com.aguilar.loginapp";

    /**
     * Test case 1: Cannot login
     * Both fields are left empty. The app should refuse to log in and show
     * a validation message instead of proceeding.
     */
    @Test
    public void testCase1_CannotLogin_EmptyFields() {
        driver.findElement(By.id(PACKAGE + ":id/etUsername")).clear();
        driver.findElement(By.id(PACKAGE + ":id/etPassword")).clear();
        driver.findElement(By.id(PACKAGE + ":id/btnLogin")).click();

        WebElement toast = waitForToast("Please enter both username and password");
        Assert.assertNotNull("Expected validation message was not displayed", toast);
    }

    /**
     * Test case 2: Wrong user
     * Correct password, incorrect username. Login should be rejected.
     */
    @Test
    public void testCase2_WrongUsername() {
        driver.findElement(By.id(PACKAGE + ":id/etUsername")).sendKeys("wronguser");
        driver.findElement(By.id(PACKAGE + ":id/etPassword")).sendKeys("Admin@123");
        driver.findElement(By.id(PACKAGE + ":id/btnLogin")).click();

        WebElement toast = waitForToast("Invalid username");
        Assert.assertNotNull("Expected 'Invalid username' message was not displayed", toast);
    }

    /**
     * Test case 3: Wrong password
     * Correct username, incorrect password. Login should be rejected.
     */
    @Test
    public void testCase3_WrongPassword() {
        driver.findElement(By.id(PACKAGE + ":id/etUsername")).sendKeys("admin");
        driver.findElement(By.id(PACKAGE + ":id/etPassword")).sendKeys("wrongpass");
        driver.findElement(By.id(PACKAGE + ":id/btnLogin")).click();

        WebElement toast = waitForToast("Invalid password");
        Assert.assertNotNull("Expected 'Invalid password' message was not displayed", toast);
    }

    private WebElement waitForToast(String expectedText) {
        return new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.presenceOfElementLocated(
                        AppiumBy.androidUIAutomator(
                                "new UiSelector().textContains(\"" + expectedText + "\")")));
    }
}
