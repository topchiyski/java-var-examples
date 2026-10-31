package com.acme.automation.support;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Lightweight mock stubs representing Selenium WebDriver, RestAssured, and Domain DTOs
 * so all examples compile and pass out-of-the-box without external browser or server dependencies.
 * <p>
 * The stubs mirror the shape of the real APIs (names, signatures, fluent chaining) closely
 * enough that the {@code var} examples read like real test code, but they return fixed
 * canned values and perform no I/O.
 */
public final class TestMocks {

    /**
     * Prevents instantiation; this class only holds nested stub types and factory methods.
     */
    private TestMocks() {
    }

    /**
     * Audit log entry returned by a stubbed audit API.
     *
     * @param status    the outcome of the audited operation, e.g. {@code "FAILED"} or {@code "SUCCESS"}
     * @param errorCode the error code, or {@code "NONE"} for successful operations
     */
    public record AuditLogDto(String status, String errorCode) {
    }

    /**
     * Result of submitting an order batch.
     *
     * @param statusCode the HTTP status code of the submission
     * @param batchId    the identifier of the submitted batch
     */
    public record OrderBatchResult(int statusCode, String batchId) {
    }

    /**
     * Stub of Selenium's {@code By} element locator.
     */
    public static class By {
        /**
         * Locator strategy and value, e.g. {@code "id:submit"}.
         */
        private final String selector;

        /**
         * Creates a locator; use the static factory methods instead.
         *
         * @param selector the locator strategy and value
         */
        private By(String selector) {
            this.selector = selector;
        }

        /**
         * Creates a locator that matches elements by CSS selector.
         *
         * @param css the CSS selector
         * @return a new locator
         */
        public static By cssSelector(String css) {
            return new By("css:" + css);
        }

        /**
         * Creates a locator that matches an element by its {@code id} attribute.
         *
         * @param id the element id
         * @return a new locator
         */
        public static By id(String id) {
            return new By("id:" + id);
        }

        /**
         * Creates a locator that matches elements by tag name.
         *
         * @param tag the tag name
         * @return a new locator
         */
        public static By tagName(String tag) {
            return new By("tag:" + tag);
        }

        /**
         * Returns the locator strategy and value, e.g. {@code "id:submit"}.
         *
         * @return the selector string
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
         * Returns the visible text of the element.
         *
         * @return the element text
         */
        String getText();

        /**
         * Returns the tag name of the element.
         *
         * @return the tag name, e.g. {@code "button"}
         */
        String getTagName();

        /**
         * Returns the value of the given attribute.
         *
         * @param name the attribute name
         * @return the attribute value
         */
        String getAttribute(String name);

        /**
         * Returns whether the element is visible.
         *
         * @return {@code true} if the element is displayed
         */
        boolean isDisplayed();

        /**
         * Clicks the element.
         */
        void click();
    }

    /**
     * {@link WebElement} with a fixed tag and text that is always displayed.
     * Every attribute lookup returns {@code "ORD-999"}.
     */
    public static class MockWebElement implements WebElement {
        /**
         * Tag name reported by {@link #getTagName()}.
         */
        private final String tag;

        /**
         * Text reported by {@link #getText()}.
         */
        private final String text;

        /**
         * Creates a mock element.
         *
         * @param tag  the tag name to report
         * @param text the visible text to report
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

        /**
         * Returns {@code "ORD-999"} regardless of the attribute name.
         *
         * @param name the attribute name (ignored)
         * @return {@code "ORD-999"}
         */
        @Override
        public String getAttribute(String name) {
            return "ORD-999";
        }

        /**
         * Always returns {@code true}.
         *
         * @return {@code true}
         */
        @Override
        public boolean isDisplayed() {
            return true;
        }

        /**
         * Does nothing.
         */
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
         *
         * @param locator the element locator
         * @return the matching element
         */
        WebElement findElement(By locator);

        /**
         * Finds all elements matching the locator.
         *
         * @param locator the element locator
         * @return the matching elements
         */
        List<WebElement> findElements(By locator);
    }

    /**
     * {@link WebDriver} that ignores the locator and returns canned elements.
     */
    public static class MockWebDriver implements WebDriver {

        /**
         * Creates a mock driver.
         */
        public MockWebDriver() {
        }

        /**
         * Returns a {@code button} element with the text {@code "EU Region Header"}.
         *
         * @param locator the element locator (ignored)
         * @return a new mock element
         */
        @Override
        public WebElement findElement(By locator) {
            return new MockWebElement("button", "EU Region Header");
        }

        /**
         * Returns two {@code tr} elements with the texts {@code "Row 1"} and {@code "Row 2"}.
         *
         * @param locator the element locator (ignored)
         * @return an immutable list of two mock elements
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
     * Stub of Selenium's {@code WebDriverListener}, which is notified of driver events.
     */
    public interface WebDriverListener {

        /**
         * Called before an element is clicked. Does nothing by default.
         *
         * @param element the element about to be clicked
         */
        default void beforeClick(WebElement element) {
        }
    }

    /**
     * Stub of Selenium's {@code EventFiringDecorator}, which wraps a driver so that a
     * {@link WebDriverListener} is notified of element clicks.
     *
     * @param <T> the type of driver being decorated
     */
    public static class EventFiringDecorator<T extends WebDriver> {
        /**
         * Listener notified before each click.
         */
        private final WebDriverListener listener;

        /**
         * Creates a decorator that notifies the given listener.
         *
         * @param listener the listener to notify
         */
        public EventFiringDecorator(WebDriverListener listener) {
            this.listener = listener;
        }

        /**
         * Wraps the driver so that clicks on elements returned by
         * {@link WebDriver#findElement(By)} call {@link WebDriverListener#beforeClick(WebElement)}
         * first. Elements returned by {@link WebDriver#findElements(By)} are not wrapped.
         *
         * @param driver the driver to decorate
         * @return the decorated driver
         */
        public WebDriver decorate(T driver) {
            return new WebDriver() {
                @Override
                public WebElement findElement(By locator) {
                    WebElement raw = driver.findElement(locator);
                    return new WebElement() {
                        @Override
                        public String getText() {
                            return raw.getText();
                        }

                        @Override
                        public String getTagName() {
                            return raw.getTagName();
                        }

                        @Override
                        public String getAttribute(String name) {
                            return raw.getAttribute(name);
                        }

                        @Override
                        public boolean isDisplayed() {
                            return raw.isDisplayed();
                        }

                        @Override
                        public void click() {
                            listener.beforeClick(raw);
                            raw.click();
                        }
                    };
                }

                @Override
                public List<WebElement> findElements(By locator) {
                    return driver.findElements(locator);
                }
            };
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
     * Stub of Selenium's {@code WebDriverWait} that evaluates the condition once,
     * immediately, without polling or waiting.
     */
    public static class WebDriverWait {
        /**
         * Driver passed to each condition.
         */
        private final WebDriver driver;

        /**
         * Creates a wait for the given driver.
         *
         * @param driver  the driver to pass to conditions
         * @param timeout the maximum wait time (ignored by this stub)
         */
        public WebDriverWait(WebDriver driver, Duration timeout) {
            this.driver = driver;
        }

        /**
         * Applies the condition to the driver once and returns its result.
         *
         * @param condition the condition to evaluate
         * @param <V>       the condition's result type
         * @return the condition's result
         */
        public <V> V until(Function<WebDriver, V> condition) {
            return condition.apply(driver);
        }
    }

    /**
     * Stub of Selenium's {@code ExpectedConditions.visibilityOfElementLocated}.
     *
     * @param locator the element locator
     * @return a condition that finds the element; visibility is not checked
     */
    public static Function<WebDriver, WebElement> visibilityOfElementLocated(By locator) {
        return driver -> driver.findElement(locator);
    }

    /**
     * Stub of RestAssured's {@code TypeRef}, a super type token: subclass it anonymously,
     * e.g. {@code new TypeRef<Map<String, Object>>() {}}, to capture a full generic type.
     *
     * @param <T> the captured type
     */
    public abstract static class TypeRef<T> {

        /**
         * Creates a type token; called by anonymous subclasses.
         */
        protected TypeRef() {
        }
    }

    /**
     * Stub of RestAssured's fluent request/response specification.
     * Every chaining method returns {@code this}.
     */
    public static class RestAssuredStub {

        /**
         * Creates a stub; use {@link TestMocks#given()} instead.
         */
        public RestAssuredStub() {
        }

        /**
         * Applies a request specification.
         *
         * @param spec the specification (ignored)
         * @return this stub
         */
        public RestAssuredStub spec(String spec) {
            return this;
        }

        /**
         * Starts the action section of the request.
         *
         * @return this stub
         */
        public RestAssuredStub when() {
            return this;
        }

        /**
         * Sends a GET request.
         *
         * @param path the request path (ignored)
         * @return this stub
         */
        public RestAssuredStub get(String path) {
            return this;
        }

        /**
         * Starts the validation section of the response.
         *
         * @return this stub
         */
        public RestAssuredStub then() {
            return this;
        }

        /**
         * Asserts the response status code.
         *
         * @param code the expected status code (not checked)
         * @return this stub
         */
        public RestAssuredStub statusCode(int code) {
            return this;
        }

        /**
         * Starts extracting values from the response.
         *
         * @return this stub
         */
        public RestAssuredStub extract() {
            return this;
        }

        /**
         * Deserializes the response body.
         * <p>
         * Unlike the real RestAssured method, which returns {@code T}, this stub always returns
         * the same canned orders-by-region map, regardless of the type token.
         *
         * @param typeRef the type token (ignored)
         * @param <T>     the requested type
         * @return a map with one {@code "EU"} region containing one shipped order
         */
        public <T> Map<String, List<Map<String, Object>>> as(TypeRef<T> typeRef) {
            return Map.of("EU", List.of(Map.of("orderId", 101, "status", "SHIPPED")));
        }
    }

    /**
     * Stub of RestAssured's {@code RestAssured.given()} entry point.
     *
     * @return a new request specification stub
     */
    public static RestAssuredStub given() {
        return new RestAssuredStub();
    }
}
