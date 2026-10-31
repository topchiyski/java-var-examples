package com.acme.automation.ugly;

import com.acme.automation.support.TestMocks.*;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Pillar 2 (The Ugly - Edge Case 3A): Non-Denotable Anonymous Class Types vs. Local Records.
 */
class AnonymousClassSpyTest {

    private final WebDriver driver = new MockWebDriver();

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

    @Test
    void structuredLocalState_SeniorPattern_LocalRecordInsteadOfAnonymousObjectBag() {
        // JUNIOR TRAP: Using 'var ctx = new Object() { String token = "xyz"; int retries = 3; };'
        // prevents reassigning 'ctx' later because every anonymous class has a distinct synthetic type.

        // SENIOR PATTERN: Use a Java 16+ local record for denotable, type-safe local test state.
        record AuthContext(String token, int retries) {}

        var initialCtx = new AuthContext("Bearer xyz", 3);
        var refreshedCtx = new AuthContext("Bearer new", 5);

        assertThat(refreshedCtx.retries()).isGreaterThan(initialCtx.retries());
    }
}
