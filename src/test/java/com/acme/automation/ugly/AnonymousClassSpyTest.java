package com.acme.automation.ugly;

import com.acme.automation.support.TestMocks.*;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Pillar 2 (The Ugly - Edge Case 3A): Non-Denotable Anonymous Class Types vs. Local Records.
 * <p>
 * {@code var} can hold a type that has no name in source code: the synthetic class of an
 * anonymous class instance. That makes the instance's extra members accessible, which is
 * useful for a quick test spy but a trap for general-purpose local state.
 */
class AnonymousClassSpyTest {

    /** Driver whose elements the spy observes. */
    private final WebDriver driver = new MockWebDriver();

    /**
     * Legitimate use: a click-counting spy declared as an anonymous {@link WebDriverListener}.
     * Because {@code var} keeps the anonymous type, the test can read {@code clickCount} and
     * {@code lastTag} and call {@code reset()} directly, without a named spy class.
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
     * Fix for ad-hoc local state: a local record gives the state a nameable type, so a
     * second instance can be created and compared, which an anonymous {@code Object}
     * subclass held in {@code var} does not allow.
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
