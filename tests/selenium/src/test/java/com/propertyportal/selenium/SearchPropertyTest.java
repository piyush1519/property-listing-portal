package com.propertyportal.selenium;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class SearchPropertyTest extends BaseTest {

    @Test
    void searchProperty() {
        String title = "Search Property " + System.currentTimeMillis();
        String location = "Mumbai Selenium Test";

        try {
            // Fill Add Property form
            driver.findElement(By.id("title")).sendKeys(title);
            driver.findElement(By.id("description"))
                    .sendKeys("Property created for Selenium search testing");
            driver.findElement(By.id("location")).sendKeys(location);
            driver.findElement(By.id("type")).sendKeys("Apartment");
            driver.findElement(By.id("price")).sendKeys("5000000");
            driver.findElement(By.id("bedrooms")).sendKeys("2");
            driver.findElement(By.id("area")).sendKeys("1000");
            driver.findElement(By.id("ownerAgent")).sendKeys("Selenium Test Agent");
            driver.findElement(By.id("status")).sendKeys("AVAILABLE");

            driver.findElement(By.cssSelector("#property-form button[type='submit']"))
                    .click();

            // Handle application alert
            wait.until(driver -> {
                try {
                    driver.switchTo().alert();
                    return true;
                } catch (Exception e) {
                    return false;
                }
            });

            String alertText = driver.switchTo().alert().getText();
            assertTrue(
                    alertText.contains("successfully"),
                    "Property creation failed. Alert: " + alertText
            );
            driver.switchTo().alert().accept();

            // Wait for the newly created property to appear
            wait.until(driver ->
                    driver.findElement(By.tagName("body"))
                            .getText()
                            .contains(title)
            );

            // Search by location
            WebElement searchLocation =
                    driver.findElement(By.id("search-location"));

            searchLocation.clear();
            searchLocation.sendKeys(location);

            driver.findElement(By.id("search-button")).click();

            // Verify search result
            wait.until(driver ->
                    driver.findElement(By.tagName("body"))
                            .getText()
                            .contains(title)
            );

            String pageText = driver.findElement(By.tagName("body")).getText();

            assertTrue(
                    pageText.contains(title),
                    "Search result does not contain the created property."
            );

            takeScreenshot("SEL-02-search-property");

        } catch (Exception e) {
            takeScreenshot("SEL-02-search-property-failed");
            throw new RuntimeException("SEL-02 Search Property failed", e);
        }
    }
}
