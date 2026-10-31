package com.acme.automation.support;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Lightweight mock stubs representing Selenium WebDriver, RestAssured, and Domain DTOs
 * so all examples compile and pass out-of-the-box without external browser or server dependencies.
 */
public final class TestMocks {

    private TestMocks() {}

    public record AuditLogDto(String status, String errorCode) {}

    public record OrderBatchResult(int statusCode, String batchId) {}

    public static class By {
        private final String selector;

        private By(String selector) {
            this.selector = selector;
        }

        public static By cssSelector(String css) {
            return new By("css:" + css);
        }

        public static By id(String id) {
            return new By("id:" + id);
        }

        public static By tagName(String tag) {
            return new By("tag:" + tag);
        }

        @Override
        public String toString() {
            return selector;
        }
    }

    public interface WebElement {
        String getText();
        String getTagName();
        String getAttribute(String name);
        boolean isDisplayed();
        void click();
    }

    public static class MockWebElement implements WebElement {
        private final String tag;
        private final String text;

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

    public interface WebDriver {
        WebElement findElement(By locator);
        List<WebElement> findElements(By locator);
    }

    public static class MockWebDriver implements WebDriver {
        @Override
        public WebElement findElement(By locator) {
            return new MockWebElement("button", "EU Region Header");
        }

        @Override
        public List<WebElement> findElements(By locator) {
            return List.of(
                new MockWebElement("tr", "Row 1"),
                new MockWebElement("tr", "Row 2")
            );
        }
    }

    public interface WebDriverListener {
        default void beforeClick(WebElement element) {}
    }

    public static class EventFiringDecorator<T extends WebDriver> {
        private final WebDriverListener listener;

        public EventFiringDecorator(WebDriverListener listener) {
            this.listener = listener;
        }

        public WebDriver decorate(T driver) {
            return new WebDriver() {
                @Override
                public WebElement findElement(By locator) {
                    WebElement raw = driver.findElement(locator);
                    return new WebElement() {
                        @Override public String getText() { return raw.getText(); }
                        @Override public String getTagName() { return raw.getTagName(); }
                        @Override public String getAttribute(String name) { return raw.getAttribute(name); }
                        @Override public boolean isDisplayed() { return raw.isDisplayed(); }
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

    public static class ChromeOptions {}

    public static class WebDriverWait {
        private final WebDriver driver;

        public WebDriverWait(WebDriver driver, Duration timeout) {
            this.driver = driver;
        }

        public <V> V until(Function<WebDriver, V> condition) {
            return condition.apply(driver);
        }
    }

    public static Function<WebDriver, WebElement> visibilityOfElementLocated(By locator) {
        return driver -> driver.findElement(locator);
    }

    public abstract static class TypeRef<T> {}

    public static class RestAssuredStub {
        public RestAssuredStub spec(String spec) { return this; }
        public RestAssuredStub when() { return this; }
        public RestAssuredStub get(String path) { return this; }
        public RestAssuredStub then() { return this; }
        public RestAssuredStub statusCode(int code) { return this; }
        public RestAssuredStub extract() { return this; }

        public <T> Map<String, List<Map<String, Object>>> as(TypeRef<T> typeRef) {
            return Map.of("EU", List.of(Map.of("orderId", 101, "status", "SHIPPED")));
        }
    }

    public static RestAssuredStub given() {
        return new RestAssuredStub();
    }
}
