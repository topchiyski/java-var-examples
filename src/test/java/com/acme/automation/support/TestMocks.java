package com.acme.automation.support;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Minimal stubs of Selenium WebDriver, RestAssured, and domain DTOs, so the examples run
 * without a browser or server. They mimic the real API shapes but return canned values.
 */
public final class TestMocks {

    /**
     * Not instantiable.
     */
    private TestMocks() {
    }

    /**
     * Audit log entry with a {@code status} such as {@code "FAILED"} and an {@code errorCode}.
     *
     * @param status    the operation outcome
     * @param errorCode the error code, or {@code "NONE"}
     */
    public record AuditLogDto(String status, String errorCode) {
    }

    /**
     * Result of submitting an order batch.
     *
     * @param statusCode the HTTP status code
     * @param batchId    the batch identifier
     */
    public record OrderBatchResult(int statusCode, String batchId) {
    }

    /**
     * Stub of Selenium's {@code By} locator.
     */
    public static class By {
        /**
         * Strategy and value, e.g. {@code "id:submit"}.
         */
        private final String selector;

        /**
         * Use the static factory methods.
         */
        private By(String selector) {
            this.selector = selector;
        }

        /**
         * Locates elements by CSS selector.
         */
        public static By cssSelector(String css) {
            return new By("css:" + css);
        }

        /**
         * Locates an element by its {@code id} attribute.
         */
        public static By id(String id) {
            return new By("id:" + id);
        }

        /**
         * Locates elements by tag name.
         */
        public static By tagName(String tag) {
            return new By("tag:" + tag);
        }

        /**
         * Returns the strategy and value, e.g. {@code "id:submit"}.
         */
        @Override
        public String toString() {
            return selector;
        }
    }

    /**
     * Stub of Selenium's {@code WebElement}.
     */
    public interface WebElement {

        /**
         * Returns the visible text.
         */
        String getText();

        /**
         * Returns the tag name, e.g. {@code "button"}.
         */
        String getTagName();

        /**
         * Returns the value of the named attribute.
         */
        String getAttribute(String name);

        /**
         * Returns whether the element is visible.
         */
        boolean isDisplayed();

        /**
         * Clicks the element.
         */
        void click();
    }

    /**
     * Always-visible element with a fixed tag and text; every attribute is {@code "ORD-999"}.
     */
    public static class MockWebElement implements WebElement {
        /**
         * Tag name to report.
         */
        private final String tag;

        /**
         * Text to report.
         */
        private final String text;

        /**
         * Creates an element with the given tag and text.
         */
        public MockWebElement(String tag, String text) {
            this.tag = tag;
            this.text = text;
        }

        @Override
        public String getText() {
            return text;
        }

        @Override
        public String getTagName() {
            return tag;
        }

        @Override
        public String getAttribute(String name) {
            return "ORD-999";
        }

        @Override
        public boolean isDisplayed() {
            return true;
        }

        @Override
        public void click() {
            // no-op click simulation
        }
    }

    /**
     * Stub of Selenium's {@code WebDriver}.
     */
    public interface WebDriver {

        /**
         * Finds the first element matching the locator.
         */
        WebElement findElement(By locator);

        /**
         * Finds all elements matching the locator.
         */
        List<WebElement> findElements(By locator);
    }

    /**
     * Driver that ignores locators and returns canned elements.
     */
    public static class MockWebDriver implements WebDriver {

        /**
         * Creates a mock driver.
         */
        public MockWebDriver() {
        }

        /**
         * Returns a {@code button} with the text {@code "EU Region Header"}.
         */
        @Override
        public WebElement findElement(By locator) {
            return new MockWebElement("button", "EU Region Header");
        }

        /**
         * Returns two {@code tr} elements, {@code "Row 1"} and {@code "Row 2"}.
         */
        @Override
        public List<WebElement> findElements(By locator) {
            return List.of(
                    new MockWebElement("tr", "Row 1"),
                    new MockWebElement("tr", "Row 2")
            );
        }
    }

    /**
     * Stub of Selenium's {@code ChromeOptions}. Has no behavior.
     */
    public static class ChromeOptions {

        /**
         * Creates empty options.
         */
        public ChromeOptions() {
        }
    }

    /**
     * Stub of Selenium's {@code WebDriverWait} that evaluates the condition once, without waiting.
     */
    public static class WebDriverWait {
        /**
         * Driver passed to conditions.
         */
        private final WebDriver driver;

        /**
         * Creates a wait; the timeout is ignored.
         */
        public WebDriverWait(WebDriver driver, Duration timeout) {
            this.driver = driver;
        }

        /**
         * Applies the condition once and returns its result.
         */
        public <V> V until(Function<WebDriver, V> condition) {
            return condition.apply(driver);
        }
    }

    /**
     * Stub of Selenium's {@code ExpectedConditions.visibilityOfElementLocated}; does not check visibility.
     */
    public static Function<WebDriver, WebElement> visibilityOfElementLocated(By locator) {
        return driver -> driver.findElement(locator);
    }

    /**
     * Stub of RestAssured's {@code TypeRef} super type token, e.g.
     * {@code new TypeRef<Map<String, Object>>() {}}.
     *
     * @param <T> the captured type
     */
    public abstract static class TypeRef<T> {

        /**
         * Called by anonymous subclasses.
         */
        protected TypeRef() {
        }
    }

    /**
     * Stub of RestAssured's fluent specification; every chaining method returns {@code this}.
     */
    public static class RestAssuredStub {

        /**
         * Use {@link TestMocks#given()}.
         */
        public RestAssuredStub() {
        }

        /**
         * Applies a request specification.
         */
        public RestAssuredStub spec(String spec) {
            return this;
        }

        /**
         * Starts the action section.
         */
        public RestAssuredStub when() {
            return this;
        }

        /**
         * Sends a GET request.
         */
        public RestAssuredStub get(String path) {
            return this;
        }

        /**
         * Starts the validation section.
         */
        public RestAssuredStub then() {
            return this;
        }

        /**
         * Stands in for the status code assertion; does not check anything.
         */
        public RestAssuredStub statusCode(int code) {
            return this;
        }

        /**
         * Starts extracting the response.
         */
        public RestAssuredStub extract() {
            return this;
        }

        /**
         * Returns a canned orders-by-region map, regardless of the type token.
         * The real method returns {@code T}.
         */
        public <T> Map<String, List<Map<String, Object>>> as(TypeRef<T> typeRef) {
            return Map.of("EU", List.of(Map.of("orderId", 101, "status", "SHIPPED")));
        }
    }

    /**
     * Stub of {@code RestAssured.given()}.
     */
    public static RestAssuredStub given() {
        return new RestAssuredStub();
    }
}
