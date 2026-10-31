package com.acme.automation.ugly;

import com.acme.automation.support.TestMocks.*;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Pillar 2 (The Ugly - Edge Case 3A): Non-Denotable Anonymous Class Types vs. Local Records.
 * <p>
 * {@code var} keeps an anonymous class's unnamed type: handy for test spies, a trap for local state.
 *
 * @see <a href="https://www.linkedin.com/pulse/why-your-java-var-code-fails-review-stoyan-topchiyski-xwome">Why Your Java var Code Fails Code Review (LinkedIn)</a>
 */
class AnonymousClassSpyTest {

    /**
     * Driver the spy observes.
     */
    private final WebDriver driver = new MockWebDriver();

    /**
     * Good: {@code var} lets the test read the anonymous spy's extra fields and methods.
     */
    @Test
    void spyWithNonDenotableVar_AdHocMembersAccessible() {
        // javac infers the non-denotable synthetic anonymous type (AnonymousClassSpyTest$1).
        // Unlike upcasting to WebDriverListener, ad-hoc fields & methods remain directly accessible!
        var clickSpy = new WebDriverListener() {
            int clickCount = 0;
            String lastTag = "";

            void reset() {
                clickCount = 0;
            }

            @Override
            public void beforeClick(WebElement el) {
                clickCount++;
                lastTag = el.getTagName();
            }
        };

        WebDriver decorated = new EventFiringDecorator<>(clickSpy).decorate(driver);
        decorated.findElement(By.id("submit")).click();

        assertThat(clickSpy.clickCount).isEqualTo(1);
        assertThat(clickSpy.lastTag).isEqualTo("button");

        clickSpy.reset();
        assertThat(clickSpy.clickCount).isZero();
    }

    /**
     * Good: use a local record, not an anonymous {@code Object}, for structured local state.
     */
    @Test
    void structuredLocalState_SeniorPattern_LocalRecordInsteadOfAnonymousObjectBag() {
        // JUNIOR TRAP: Using 'var ctx = new Object() { String token = "xyz"; int retries = 3; };'
        // prevents reassigning 'ctx' later because every anonymous class has a distinct synthetic type.

        // SENIOR PATTERN: Use a Java 16+ local record for denotable, type-safe local test state.
        record AuthContext(String token, int retries) {
        }

        var initialCtx = new AuthContext("Bearer xyz", 3);
        var refreshedCtx = new AuthContext("Bearer new", 5);

        assertThat(refreshedCtx.retries()).isGreaterThan(initialCtx.retries());
    }
}
