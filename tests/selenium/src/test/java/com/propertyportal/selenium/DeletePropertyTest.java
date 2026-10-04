package com.propertyportal.selenium;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DeletePropertyTest extends BaseTest {

    @Test
    void deleteProperty() {
        String title = "Delete Property " + System.currentTimeMillis();

        try {
            // Create property
            driver.findElement(By.id("title")).sendKeys(title);
            driver.findElement(By.id("description"))
                    .sendKeys("Property created for Selenium delete testing");
            driver.findElement(By.id("location")).sendKeys("Mumbai Delete Test");
            driver.findElement(By.id("type")).sendKeys("Apartment");
            driver.findElement(By.id("price")).sendKeys("4500000");
            driver.findElement(By.id("bedrooms")).sendKeys("2");
            driver.findElement(By.id("area")).sendKeys("950");
            driver.findElement(By.id("ownerAgent")).sendKeys("Selenium Delete Agent");
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

            // Wait until property appears
            wait.until(driver ->
                    driver.findElement(By.tagName("body"))
                            .getText()
                            .contains(title)
            );

            // Find the property card
            List<WebElement> cards =
                    driver.findElements(By.cssSelector(".property-card"));

            WebElement targetCard = null;

            for (WebElement card : cards) {
                if (card.getText().contains(title)) {
                    targetCard = card;
                    break;
                }
            }

            assertTrue(
                    targetCard != null,
                    "Created property card was not found."
            );

            // Click Delete
            WebElement deleteButton =
                    targetCard.findElement(By.cssSelector(".delete-button"));

            deleteButton.click();

            // Handle confirmation alert
            wait.until(driver -> {
                try {
                    driver.switchTo().alert();
                    return true;
                } catch (Exception e) {
                    return false;
                }
            });

            Alert deleteAlert = driver.switchTo().alert();

            String alertText = deleteAlert.getText();

            // Most implementations use confirm() for deletion.
            // Accept the confirmation.
            deleteAlert.accept();

            // Handle deletion success alert
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
                    successAlert.getText().contains("Property deleted successfully"),
                    "Unexpected deletion result: " + successAlert.getText()
            );

            successAlert.accept();

            // Wait until the property disappears
            wait.until(driver -> {
                String bodyText = driver.findElement(By.tagName("body")).getText();
                return !bodyText.contains(title);
            });

            String pageText = driver.findElement(By.tagName("body")).getText();

            assertFalse(
                    pageText.contains(title),
                    "Deleted property is still displayed."
            );

            takeScreenshot("SEL-04-delete-property");

        } catch (Exception e) {
            takeScreenshot("SEL-04-delete-property-failed");
            throw new RuntimeException("SEL-04 Delete Property failed", e);
        }
    }
}
