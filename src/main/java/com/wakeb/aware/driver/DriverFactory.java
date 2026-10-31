package com.wakeb.aware.driver;

import com.wakeb.aware.config.ConfigReader;
import java.io.IOException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class DriverFactory {

    public static WebDriver createDriver() throws IOException {

        String browser = ConfigReader.get("browser");

        if (!"chrome".equalsIgnoreCase(browser)) {
            throw new IllegalArgumentException("Only Chrome is supported");
        }

        ChromeOptions options = new ChromeOptions();
        options.setExperimentalOption("detach", true);

        boolean headless = Boolean.parseBoolean(
                ConfigReader.get("headless")
        );

        if (headless) {
            options.addArguments("--headless=new");
        }

        WebDriver driver = new ChromeDriver(options);
        driver.manage().window().maximize();

        return driver;
    }
}
