package com.wakeb.aware.pages;

import com.wakeb.aware.config.ConfigReader;
import java.io.IOException;
import java.nio.file.Path;
import java.time.Duration;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class TicketsPage {

    private WebDriver driver;
    private WebDriverWait wait;

    private By pageTitle = By.xpath("//h2[normalize-space()='Tickets']");
    private By reportButton = By.xpath("//button[normalize-space()='Report']");
    private By arabicTitleField = By.cssSelector("input[placeholder='Report title in Arabic']");
    private By englishTitleField = By.cssSelector("input[placeholder='Report title in English']");
    private By arabicDescription = By.cssSelector("textarea[placeholder='Description in Arabic']");
    private By englishDescription = By.cssSelector("textarea[placeholder='Description in English']");
    private By fileInput = By.cssSelector("input[type='file']");
    private By confirmReport = By.xpath("//button[normalize-space()='Confirm report']");
    private By ticketSearch = By.cssSelector(".table-search input[placeholder='Search']");
    private By details = By.cssSelector(".card-detail-modal");
    private By closeDetails = By.cssSelector(".card-detail-modal button[aria-label='Close']");
    private By stageField = By.cssSelector(".card-detail-modal [updatekey='step.id'] input[role='combobox']");
    private By priorityField = By.cssSelector(".card-detail-modal .field[usetype='enum'] input[role='combobox']");
    private By commentField = By.cssSelector("textarea[placeholder='Enter Add Comment']");
    private By addCommentButton = By.xpath("//div[contains(@class,'card-detail-modal')]//button[normalize-space()='Add Comment']");
    private By historyTab = By.xpath("//div[contains(@class,'card-detail-modal')]//button[normalize-space()='History']");

    public TicketsPage(WebDriver driver) throws IOException {
        this.driver = driver;
        int timeout = Integer.parseInt(ConfigReader.get("timeout.seconds"));
        wait = new WebDriverWait(driver, Duration.ofSeconds(timeout));
    }

    public void open() throws IOException {
        driver.get(ConfigReader.get("base.url") + "/tickets");
        wait.until(ExpectedConditions.visibilityOfElementLocated(pageTitle));
    }

    public void openReportForm(int violationNumber) throws IOException {
        driver.get(ConfigReader.get("base.url") + "/events/view-event/" + violationNumber);
        click(reportButton);
        wait.until(ExpectedConditions.visibilityOfElementLocated(englishTitleField));
    }

    public void enterReportTitles(String arabicTitle, String englishTitle) {
        type(arabicTitleField, arabicTitle);
        type(englishTitleField, englishTitle);
    }

    public void chooseReportOptions(String priority, String phase, String detectionType, String reportTo) {
        click(By.xpath("//button[normalize-space()='" + priority + "']"));
        selectOption(reportField("Phase"), phase);
        selectOption(reportField("Detection Type"), detectionType);
        selectOption(reportField("Report to"), reportTo);
    }

    public void enterDescriptions(String arabicText, String englishText) {
        type(arabicDescription, arabicText);
        type(englishDescription, englishText);
    }

    public void uploadAttachment(String filePath) {
        Path attachment = Path.of(filePath).toAbsolutePath();
        wait.until(ExpectedConditions.presenceOfElementLocated(fileInput))
                .sendKeys(attachment.toString());
        wait.until(ExpectedConditions.textToBePresentInElementLocated(
                By.cssSelector("[role='dialog']"), attachment.getFileName().toString()));
    }

    public void submitReport() {
        click(confirmReport);
        wait.until(ExpectedConditions.invisibilityOfElementLocated(englishTitleField));
    }

    public void searchTicket(String title) {
        WebElement search = wait.until(ExpectedConditions.visibilityOfElementLocated(ticketSearch));
        if (!title.equals(search.getDomProperty("value"))) {
            search.clear();
            search.sendKeys(title);
        }
        wait.until(ExpectedConditions.numberOfElementsToBe(
                By.cssSelector("tr.v-data-table__tr"), 1));
        wait.until(ExpectedConditions.visibilityOfElementLocated(ticketRow(title)));
    }

    public String getTicketRowText(String title) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(ticketRow(title))).getText();
    }

    public void openTicket(String title) {
        searchTicket(title);
        click(By.xpath(rowXPath(title) + "//button[contains(@class,'btn--table-icon')]"));
        WebElement view = wait.until(d -> {
            for (WebElement button : d.findElements(
                    By.cssSelector("button[role='menuitem'][data-voice-action='view']"))) {
                if (button.isDisplayed() && button.isEnabled()) {
                    return button;
                }
            }
            return null;
        });
        view.click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[contains(@class,'card-detail-modal')]//h2[normalize-space()='" + title + "']")));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".card-detail-modal [updatekey='step.id'] div[role='combobox']")));
    }

    public String getStage() {
        return wait.until(ExpectedConditions.presenceOfElementLocated(stageField)).getDomProperty("value");
    }

    public String getPriority() {
        return wait.until(ExpectedConditions.presenceOfElementLocated(priorityField)).getDomProperty("value");
    }

    public String getDetailsText() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(details)).getText();
    }

    public boolean hasAttachment() {
        return !driver.findElements(By.cssSelector(".card-detail-modal a[href*='/storage/']")).isEmpty();
    }

    public void changePriority(String priority) {
        selectOption(priorityField, priority);
        wait.until(ExpectedConditions.textToBePresentInElementValue(priorityField, priority));
    }

    public void changeStage(String stage) {
        selectOption(stageField, stage);
        wait.until(ExpectedConditions.textToBePresentInElementValue(stageField, stage));
    }

    public void addComment(String comment) {
        type(commentField, comment);
        click(addCommentButton);
        wait.until(ExpectedConditions.textToBePresentInElementLocated(details, comment));
        wait.until(d -> d.findElement(commentField).getDomProperty("value").isEmpty());
    }

    public void openHistory() {
        click(historyTab);
    }

    public void close() {
        click(closeDetails);
        wait.until(ExpectedConditions.invisibilityOfElementLocated(details));
    }

    private By reportField(String label) {
        return By.xpath("//div[contains(@class,'field-label') and normalize-space()='"
                + label + "']/..//input[@role='combobox']");
    }

    private String rowXPath(String title) {
        return "//tr[.//h2[normalize-space()='" + title + "']]";
    }

    private By ticketRow(String title) {
        return By.xpath(rowXPath(title));
    }

    private void selectOption(By field, String optionText) {
        WebElement input = wait.until(ExpectedConditions.presenceOfElementLocated(field));
        WebElement dropdown = input.findElement(
                By.xpath("ancestor::div[@role='combobox'][1]"));
        wait.until(ExpectedConditions.elementToBeClickable(dropdown)).click();
        WebElement option = wait.until(d -> {
            for (WebElement item : d.findElements(By.cssSelector("div[role='option']"))) {
                if (item.isDisplayed() && item.getText().trim().equals(optionText)) {
                    return item;
                }
            }
            return null;
        });
        if (!"true".equals(option.getDomAttribute("aria-selected"))) {
            option.click();
        }
        driver.switchTo().activeElement().sendKeys(Keys.ESCAPE);
        wait.until(ExpectedConditions.attributeToBe(field, "aria-expanded", "false"));
    }

    private void click(By locator) {
        wait.until(ExpectedConditions.elementToBeClickable(locator)).click();
    }

    private void type(By locator, String text) {
        WebElement field = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        field.clear();
        field.sendKeys(text);
    }
}