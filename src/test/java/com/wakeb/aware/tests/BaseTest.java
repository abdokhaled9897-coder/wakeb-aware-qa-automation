package com.wakeb.aware.tests;

import com.wakeb.aware.driver.DriverFactory;
import java.io.IOException;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

public class BaseTest {

    protected WebDriver driver;
    protected boolean keepBrowserOpen;

    @BeforeMethod
    public void setUp() throws IOException {
        keepBrowserOpen = false;
        driver = DriverFactory.createDriver();
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() throws InterruptedException {
        if (driver != null && !keepBrowserOpen) {
            Thread.sleep(5000);
            driver.quit();
        }
    }
}