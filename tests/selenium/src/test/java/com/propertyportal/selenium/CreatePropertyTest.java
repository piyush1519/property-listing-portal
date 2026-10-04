package com.propertyportal.selenium;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class CreatePropertyTest extends BaseTest {

    @Test
    void createProperty() {

        String title =
                "Selenium Property "
                        + System.currentTimeMillis();

        try {

            driver.findElement(By.id("title"))
                    .sendKeys(title);

            driver.findElement(By.id("description"))
                    .sendKeys(
                            "Property created by Selenium automation test."
                    );

            driver.findElement(By.id("location"))
                    .sendKeys("Mumbai");

            driver.findElement(By.id("type"))
                    .sendKeys("Apartment");

            driver.findElement(By.id("price"))
                    .sendKeys("7500000");

            driver.findElement(By.id("bedrooms"))
                    .sendKeys("2");

            driver.findElement(By.id("area"))
                    .sendKeys("1200");

            driver.findElement(By.id("ownerAgent"))
                    .sendKeys("Selenium Test Agent");

            driver.findElement(By.id("status"))
                    .clear();

            driver.findElement(By.id("status"))
                    .sendKeys("AVAILABLE");

            driver.findElement(
                    By.cssSelector(
                            "#property-form button[type='submit']"
                    )
            ).click();

            wait.until(
                    ExpectedConditions.alertIsPresent()
            );

            driver.switchTo()
                    .alert()
                    .accept();

            wait.until(driver -> {

                String bodyText =
                        driver.findElement(
                                By.tagName("body")
                        ).getText();

                return bodyText.contains(title);
            });

            String pageText =
                    driver.findElement(
                            By.tagName("body")
                    ).getText();

            assertTrue(
                    pageText.contains(title),
                    "Created property should appear in the listing"
            );

        } catch (Exception e) {

            takeScreenshot(
                    "SEL-01-create-property-failure"
            );

            throw new RuntimeException(
                    "SEL-01 Create Property failed",
                    e
            );
        }
    }
}
