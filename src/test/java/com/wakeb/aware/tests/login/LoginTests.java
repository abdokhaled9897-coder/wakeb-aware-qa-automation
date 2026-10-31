package com.wakeb.aware.tests.login;

import com.wakeb.aware.pages.LoginPage;
import com.wakeb.aware.tests.BaseTest;
import java.io.IOException;
import java.time.Duration;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class LoginTests extends BaseTest {

    private LoginPage loginPage;
    private WebDriverWait wait;

    private String validEmail = System.getenv("AWARE_EMAIL");
    private String validPassword = System.getenv("AWARE_PASSWORD");

    @BeforeMethod
    public void openLoginPage() throws IOException {
        Assert.assertNotNull(validEmail, "Set AWARE_EMAIL before running.");
        Assert.assertNotNull(validPassword, "Set AWARE_PASSWORD before running.");

        loginPage = new LoginPage(driver);

        wait = new WebDriverWait(
                driver, Duration.ofSeconds(15)
        );

        loginPage.open();
    }

    @Test(priority = 1)
    public void emptyEmailAndPassword() {
        loginPage.enterEmail("");
        loginPage.enterPassword("");

        signIn();

        String actualMessage = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector(".v-messages__message")
                )
        ).getText();

        Assert.assertEquals(
                actualMessage,
                "Email field is required."
        );

        Assert.assertTrue(
                driver.getCurrentUrl().contains("/login")
        );
    }

    @Test(priority = 2)
    public void correctEmailAndWrongPassword() {
        loginPage.enterEmail(validEmail);
        loginPage.enterPassword("WrongPassword123!");

        signIn();
        checkLoginError();
    }

    @Test(priority = 3)
    public void wrongEmailAndCorrectPassword() {
        loginPage.enterEmail("wrong.user@example.com");
        loginPage.enterPassword(validPassword);

        signIn();
        checkLoginError();
    }

    @Test(priority = 4)
    public void wrongEmailAndWrongPassword() {
        loginPage.enterEmail("wrong.user@example.com");
        loginPage.enterPassword("WrongPassword123!");

        signIn();
        checkLoginError();
    }

    @Test(priority = 5)
    public void showAndHidePassword() {
        String samplePassword = "TestPassword123!";

        By passwordField = By.cssSelector(
                "input[placeholder='Enter Password']"
        );

        By passwordToggle = By.cssSelector(
                "button.password-field__toggle"
        );

        loginPage.enterPassword(samplePassword);

        Assert.assertEquals(
                driver.findElement(passwordField).getDomProperty("type"),
                "password"
        );

        driver.findElement(passwordToggle).click();

        Assert.assertEquals(
                driver.findElement(passwordField).getDomProperty("type"),
                "text"
        );

        Assert.assertEquals(
                driver.findElement(passwordField).getDomProperty("value"),
                samplePassword
        );

        driver.findElement(passwordToggle).click();

        Assert.assertEquals(
                driver.findElement(passwordField).getDomProperty("type"),
                "password"
        );

        Assert.assertEquals(
                driver.findElement(passwordField).getDomProperty("value"),
                samplePassword
        );
    }

    @Test(priority = 6)
    public void successfulLogin() {
        loginPage.enterEmail(validEmail);
        loginPage.enterPassword(validPassword);

        signIn();

        wait.until(
                ExpectedConditions.urlContains("/events")
        );

        Assert.assertTrue(
                wait.until(
                        ExpectedConditions.visibilityOfElementLocated(
                                By.xpath("//h2[normalize-space()='Events']")
                        )
                ).isDisplayed()
        );

        keepBrowserOpen = true;
    }

    private void signIn() {
        System.out.println(
                "Complete the CAPTCHA manually in the browser."
        );

        WebDriverWait captchaWait = new WebDriverWait(
                driver, Duration.ofSeconds(120)
        );

        captchaWait.until(
                ExpectedConditions.elementToBeClickable(
                        By.cssSelector("button[type='submit']")
                )
        );

        loginPage.clickSignIn();
    }

    private void checkLoginError() {
        String actualMessage = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.id("swal2-title")
                )
        ).getText();

        Assert.assertEquals(
                actualMessage,
                "Email or password is incorrect"
        );

        Assert.assertTrue(
                driver.getCurrentUrl().contains("/login")
        );
    }
}