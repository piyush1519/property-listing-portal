package com.propertyportal.selenium;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class UpdatePropertyTest extends BaseTest {

    @Test
    void updateProperty() {
        String originalTitle = "Update Property " + System.currentTimeMillis();
        String updatedTitle = originalTitle + " Updated";

        try {
            // Create a property first
            driver.findElement(By.id("title")).sendKeys(originalTitle);
            driver.findElement(By.id("description"))
                    .sendKeys("Property created for Selenium update testing");
            driver.findElement(By.id("location")).sendKeys("Mumbai Update Test");
            driver.findElement(By.id("type")).sendKeys("Apartment");
            driver.findElement(By.id("price")).sendKeys("6000000");
            driver.findElement(By.id("bedrooms")).sendKeys("2");
            driver.findElement(By.id("area")).sendKeys("1200");
            driver.findElement(By.id("ownerAgent")).sendKeys("Selenium Update Agent");
            driver.findElement(By.id("status")).sendKeys("AVAILABLE");

            driver.findElement(By.cssSelector("#property-form button[type='submit']"))
                    .click();

            // Handle creation alert
            wait.until(driver -> {
                try {
                    driver.switchTo().alert();
                    return true;
                } catch (Exception e) {
                    return false;
                }
            });

            Alert creationAlert = driver.switchTo().alert();

            assertTrue(
                    creationAlert.getText().contains("successfully"),
                    "Property creation failed: " + creationAlert.getText()
            );

            creationAlert.accept();

            // Wait for the property to appear
            wait.until(driver ->
                    driver.findElement(By.tagName("body"))
                            .getText()
                            .contains(originalTitle)
            );

            // Find the property card containing our title
            List<WebElement> cards =
                    driver.findElements(By.cssSelector(".property-card"));

            WebElement targetCard = null;

            for (WebElement card : cards) {
                if (card.getText().contains(originalTitle)) {
                    targetCard = card;
                    break;
                }
            }

            assertTrue(
                    targetCard != null,
                    "Created property card was not found."
            );

            // Click Edit for this property
            WebElement editButton =
                    targetCard.findElement(By.cssSelector(".edit-button"));

            editButton.click();

            // Edit title prompt
            wait.until(driver -> {
                try {
                    driver.switchTo().alert();
                    return true;
                } catch (Exception e) {
                    return false;
                }
            });

            Alert titleAlert = driver.switchTo().alert();
            assertTrue(
                    titleAlert.getText().contains("Enter property title"),
                    "Unexpected title prompt: " + titleAlert.getText()
            );
            titleAlert.sendKeys(updatedTitle);
            titleAlert.accept();

            // Edit price prompt
            wait.until(driver -> {
                try {
                    driver.switchTo().alert();
                    return true;
                } catch (Exception e) {
                    return false;
                }
            });

            Alert priceAlert = driver.switchTo().alert();
            assertTrue(
                    priceAlert.getText().contains("Enter property price"),
                    "Unexpected price prompt: " + priceAlert.getText()
            );
            priceAlert.sendKeys("6500000");
            priceAlert.accept();

            // Edit status prompt
            wait.until(driver -> {
                try {
                    driver.switchTo().alert();
                    return true;
                } catch (Exception e) {
                    return false;
                }
            });

            Alert statusAlert = driver.switchTo().alert();
            assertTrue(
                    statusAlert.getText().contains("Enter status"),
                    "Unexpected status prompt: " + statusAlert.getText()
            );
            statusAlert.sendKeys("SOLD");
            statusAlert.accept();

            // Handle update confirmation alert
            wait.until(driver -> {
                try {
                    driver.switchTo().alert();
                    return true;
                } catch (Exception e) {
                    return false;
                }
            });

            Alert successAlert = driver.switchTo().alert();

            assertTrue(
                    successAlert.getText().contains("successfully"),
                    "Property update failed: " + successAlert.getText()
            );

            successAlert.accept();

            // Verify updated title
            wait.until(driver ->
                    driver.findElement(By.tagName("body"))
                            .getText()
                            .contains(updatedTitle)
            );

            String pageText = driver.findElement(By.tagName("body")).getText();

            assertTrue(
                    pageText.contains(updatedTitle),
                    "Updated property title was not found."
            );

            takeScreenshot("SEL-03-update-property");

        } catch (Exception e) {
            takeScreenshot("SEL-03-update-property-failed");
            throw new RuntimeException("SEL-03 Update Property failed", e);
        }
    }
}
