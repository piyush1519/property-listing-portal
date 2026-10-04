package com.propertyportal.selenium;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;

public abstract class BaseTest {

    protected WebDriver driver;
    protected WebDriverWait wait;

    protected static final String BASE_URL = "http://localhost:5500";

    @BeforeEach
    void setUp() {

        driver = new ChromeDriver();

        driver.manage().window().maximize();

        wait = new WebDriverWait(
                driver,
                Duration.ofSeconds(10)
        );

        driver.get(BASE_URL);
    }

    @AfterEach
    void tearDown(org.junit.jupiter.api.TestInfo testInfo) {

        if (driver != null) {

            if (testInfo != null) {
                // Screenshot handling is performed by individual tests
                // when an assertion fails.
            }

            driver.quit();
        }
    }

    protected void takeScreenshot(String name) {

        try {

            File source =
                    ((TakesScreenshot) driver)
                            .getScreenshotAs(OutputType.FILE);

            Path destination =
                    Path.of(
                            "screenshots",
                            name + ".png"
                    );

            Files.copy(
                    source.toPath(),
                    destination,
                    java.nio.file.StandardCopyOption.REPLACE_EXISTING
            );

        } catch (Exception e) {

            System.out.println(
                    "Could not capture screenshot: "
                            + e.getMessage()
            );
        }
    }
}
