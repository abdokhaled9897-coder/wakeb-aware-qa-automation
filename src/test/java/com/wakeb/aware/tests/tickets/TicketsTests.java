package com.wakeb.aware.tests.tickets;

import com.wakeb.aware.config.ConfigReader;
import com.wakeb.aware.pages.LoginPage;
import com.wakeb.aware.pages.TicketsPage;
import com.wakeb.aware.tests.BaseTest;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;

public class TicketsTests extends BaseTest {

    @Test
    public void ticketLifecycle() throws IOException {
        String email = System.getenv("AWARE_EMAIL");
        String password = System.getenv("AWARE_PASSWORD");
        Assert.assertNotNull(email, "Set AWARE_EMAIL before running.");
        Assert.assertNotNull(password, "Set AWARE_PASSWORD before running.");

        Path attachment = Path.of("src/test/resources/testdata/ticket-evidence.png");
        Assert.assertTrue(Files.isRegularFile(attachment), "The ticket attachment is missing.");

        LoginPage loginPage = new LoginPage(driver);
        loginPage.open();
        loginPage.enterEmail(email);
        loginPage.enterPassword(password);

        System.out.println("Complete the CAPTCHA manually. Selenium will click Sign In.");
        new WebDriverWait(driver, Duration.ofSeconds(120)).until(
                ExpectedConditions.elementToBeClickable(By.cssSelector("button[type='submit']")));
        loginPage.clickSignIn();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        wait.until(ExpectedConditions.urlContains("/events"));
        Assert.assertTrue(wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//h2[normalize-space()='Events']"))).isDisplayed());

        String runId = String.valueOf(System.currentTimeMillis());
        String title = "Visitor Review " + runId;
        String description = "Review visitor count at the main warehouse entrance";
        String comment = "Visitor count checked during warehouse follow up " + runId;

        TicketsPage ticketsPage = new TicketsPage(driver);
        int violationNumber = Integer.parseInt(ConfigReader.get("ticket.violation.id"));
        ticketsPage.openReportForm(violationNumber);
        ticketsPage.enterReportTitles("مراجعة عدد زوار المستودع", title);
        ticketsPage.chooseReportOptions("Medium", "New",
                ConfigReader.get("ticket.detection.type"), ConfigReader.get("ticket.report.to"));
        ticketsPage.enterDescriptions("مراجعة عدد الزوار عند مدخل المستودع الرئيسي", description);
        ticketsPage.uploadAttachment(attachment.toString());
        ticketsPage.submitReport();
        System.out.println("Created ticket: " + title);

        ticketsPage.open();
        ticketsPage.searchTicket(title);
        String row = ticketsPage.getTicketRowText(title);
        Assert.assertTrue(row.contains(title), "The new ticket is missing from the table.");
        Assert.assertTrue(row.contains("New"), "The initial stage should be New.");
        Assert.assertTrue(row.contains("Medium"), "The initial priority should be Medium.");


        ticketsPage.openTicket(title);
        Assert.assertEquals(ticketsPage.getStage(), "New");
        Assert.assertEquals(ticketsPage.getPriority(), "Medium");
        Assert.assertTrue(ticketsPage.getDetailsText().contains(description));
        Assert.assertTrue(ticketsPage.getDetailsText().contains(ConfigReader.get("ticket.detection.type"))); 
        Assert.assertTrue(ticketsPage.getDetailsText().contains(ConfigReader.get("ticket.report.to")));
        Assert.assertTrue(ticketsPage.hasAttachment(), "The saved ticket attachment is missing.");

        ticketsPage.addComment(comment);
        ticketsPage.changePriority("High");
        ticketsPage.close();
        ticketsPage.openTicket(title);
        Assert.assertEquals(ticketsPage.getPriority(), "High", "Priority was not saved.");
        Assert.assertTrue(ticketsPage.getDetailsText().contains(comment), "Comment was not saved.");

        ticketsPage.changeStage("In Analysis");
        ticketsPage.openHistory();
        Assert.assertTrue(wait.until(ExpectedConditions.textToBePresentInElementLocated(
                By.cssSelector(".card-detail-modal"), "to In Analysis")));
        ticketsPage.close();
        ticketsPage.openTicket(title);
        Assert.assertEquals(ticketsPage.getStage(), "In Analysis", "Stage was not saved.");

        ticketsPage.changeStage("Resolved");
        ticketsPage.openHistory();
        Assert.assertTrue(wait.until(ExpectedConditions.textToBePresentInElementLocated(
                By.cssSelector(".card-detail-modal"), "to Resolved")));
        ticketsPage.close();
        ticketsPage.openTicket(title);
        Assert.assertEquals(ticketsPage.getStage(), "Resolved", "The ticket was not resolved.");
        Assert.assertEquals(ticketsPage.getPriority(), "High");
        Assert.assertTrue(ticketsPage.getDetailsText().contains(comment));
        ticketsPage.close();

        row = ticketsPage.getTicketRowText(title);
        Assert.assertTrue(row.contains("Resolved"), "The table should show Resolved.");
        Assert.assertTrue(row.contains("High"), "The table should show High priority.");
        System.out.println("Ticket lifecycle passed: " + title);
    }
}