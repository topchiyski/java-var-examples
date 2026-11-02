package com.acme.automation.ugly;

import com.acme.automation.support.TestMocks.*;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * With {@code var}, the diamond operator has no target type, so without constructor arguments
 * {@code <>} infers {@code Object}.
 */
class DiamondOperatorTrapTest {

    /**
     * Source of the list elements.
     */
    private final WebDriver driver = new MockWebDriver();

    /**
     * Trap: {@code var} with {@code new ArrayList<>()} accepts anything and fails at runtime.
     */
    @Test
    void diamondOperatorTrap_Junior_SilentlyInfersObject() {
        // TRAP: Pairing 'var' with empty '<>' provides zero type constraints,
        // causing javac to fall back to ArrayList<Object>!
        var activeElements = new ArrayList<>();

        activeElements.add(driver.findElement(By.id("row-1")));
        // BUG COMPILES SILENTLY: Added a 'By' locator and a 'String' instead of WebElement!
        activeElements.add(By.id("row-2"));
        activeElements.add("#row-3");

        // Explodes at runtime with ClassCastException!
        assertThatThrownBy(() -> {
            for (var el : activeElements) {
                ((WebElement) el).isDisplayed();
            }
        }).isInstanceOf(ClassCastException.class);
    }

    /**
     * Fix: supply the type on the right-hand side or through a typed constructor argument.
     */
    @Test
    void diamondOperatorSafe_SeniorPattern_ExplicitRhsOrConstructorArg() {
        // Option 1: Put the explicit generic type on the right-hand constructor
        var activeElements = new ArrayList<WebElement>();
        activeElements.add(driver.findElement(By.id("row-1")));
        // activeElements.add(By.id("row-2")); // <-- Caught immediately at compile time!

        // Option 2: Pass a typed collection to the constructor so '<>' infers WebElement
        List<WebElement> rows = driver.findElements(By.cssSelector("tr"));
        var copiedElements = new ArrayList<>(rows);

        assertThat(activeElements).hasSize(1);
        assertThat(copiedElements).hasSize(2).allMatch(WebElement::isDisplayed);
    }
}
