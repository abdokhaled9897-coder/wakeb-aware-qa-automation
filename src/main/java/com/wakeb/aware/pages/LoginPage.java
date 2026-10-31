package com.wakeb.aware.pages;

import com.wakeb.aware.config.ConfigReader;
import java.io.IOException;
import java.time.Duration;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class LoginPage {

    private WebDriver driver;
    private WebDriverWait wait;

    private By emailField =
            By.cssSelector("input[placeholder='Enter Email']");

    private By passwordField =
            By.cssSelector("input[placeholder='Enter Password']");

    private By signInButton =
            By.cssSelector("button[type='submit']");

    public LoginPage(WebDriver driver) throws IOException {
        this.driver = driver;

        int timeout = Integer.parseInt(
                ConfigReader.get("timeout.seconds")
        );

        wait = new WebDriverWait(driver, Duration.ofSeconds(timeout));
    }

    public void open() throws IOException {
        driver.get(ConfigReader.get("base.url") + "/login");

        wait.until(
                ExpectedConditions.visibilityOfElementLocated(emailField)
        );
    }

    public void enterEmail(String email) {
        wait.until(
                ExpectedConditions.visibilityOfElementLocated(emailField)
        ).clear();

        driver.findElement(emailField).sendKeys(email);
    }

    public void enterPassword(String password) {
        wait.until(
                ExpectedConditions.visibilityOfElementLocated(passwordField)
        ).clear();

        driver.findElement(passwordField).sendKeys(password);
    }

    public void clickSignIn() {
        wait.until(
                ExpectedConditions.elementToBeClickable(signInButton)
        ).click();
    }
}