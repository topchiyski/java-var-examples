package com.acme.automation.bad;

import com.acme.automation.support.TestMocks.*;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Pillar 2 (The Bad): Opaque Helper Calls &amp; Wide Variable Scope Bugs
 * (Principles P2, P3 &amp; Guidelines G1, G2).
 * <p>
 * {@code var} removes the type from the declaration, so the variable name and the
 * initializer must carry that information instead. These tests contrast code that is
 * unreadable outside an IDE (for example, in a pull request diff) with code that
 * stays readable.
 */
class ReadabilityAndScopeTest {

    // Opaque helper names (anti-pattern)

    /**
     * Opaque helper: the name does not reveal that it returns a {@link By} locator.
     *
     * @param id the element id
     * @return a locator for the id
     */
    private By get(String id) {
        return By.id(id);
    }

    /**
     * Opaque helper: the name does not reveal that it returns a {@link WebElement}.
     *
     * @param locator the element locator (ignored)
     * @return a mock submit button
     */
    private WebElement find(By locator) {
        return new MockWebElement("button", "Submit");
    }

    /**
     * Opaque helper: the name does not reveal what it processes or what it returns.
     *
     * @param id the order id
     * @return a successful result for the id
     */
    private OrderBatchResult processData(String id) {
        return new OrderBatchResult(200, id);
    }

    /**
     * Opaque helper with no arguments, used to show that an explicit type keeps
     * the call readable when the method name cannot be changed.
     *
     * @return a successful result for batch {@code "BATCH-1"}
     */
    private OrderBatchResult processData() {
        return new OrderBatchResult(200, "BATCH-1");
    }

    // Descriptive helper names (senior pattern)

    /**
     * Descriptive helper: builds a locator for a test id.
     *
     * @param id the element id
     * @return a locator for the id
     */
    private By buildTestIdLocator(String id) {
        return By.id(id);
    }

    /**
     * Descriptive helper: waits until the located element is visible.
     *
     * @param locator the element locator (ignored)
     * @return a mock submit button
     */
    private WebElement waitForVisibleElement(By locator) {
        return new MockWebElement("button", "Submit");
    }

    /**
     * Descriptive helper: submits the order with the given id.
     *
     * @param id the order id
     * @return a successful submission result
     */
    private OrderBatchResult submitOrderById(String id) {
        return new OrderBatchResult(200, id);
    }

    /**
     * Descriptive helper: returns the currently open modal dialog.
     *
     * @return a mock modal element
     */
    private WebElement getActiveModal() {
        return new MockWebElement("div", "Modal");
    }

    /**
     * Returns order ids in a fixed order.
     *
     * @return the ids {@code "ORD-1"} and {@code "ORD-2"}, in that order
     */
    private List<String> fetchIds() {
        return List.of("ORD-1", "ORD-2");
    }

    /**
     * Anti-pattern: short variable names combined with opaque helper names.
     * Without IDE type hints, a reviewer cannot tell what {@code r}, {@code el},
     * or {@code res} are.
     */
    @Test
    void checkoutFlow_Bad_OpaqueVarInBrowserDiff() {
        // BAD: In a GitHub PR diff without IDE tooltips, what types are 'r', 'el', and 'res'?
        var r = get("checkout-btn");
        var el = find(r);
        var res = processData(el.getAttribute("data-id"));

        assertThat(res.statusCode()).isEqualTo(200);
    }

    /**
     * Fix: type information is carried by at least one of three channels — the variable
     * name, the method name, or an explicit type. With {@code var}, the variable and
     * method names must carry it; when an opaque method cannot be renamed, keep the
     * explicit type.
     */
    @Test
    void checkoutFlow_Good_ThreeChannelConservationLaw() {
        // GOOD: Descriptive variable & method names carry the semantic weight (G1, G3),
        // or explicit types are preserved when calling an opaque method like processData().
        var checkoutBtnLocator = buildTestIdLocator("checkout-btn");
        var checkoutSubmitBtn = waitForVisibleElement(checkoutBtnLocator);
        var orderSubmissionResponse = submitOrderById(checkoutSubmitBtn.getAttribute("data-id"));

        OrderBatchResult explicitResultFromOpaqueHelper = processData();

        assertThat(orderSubmissionResponse.statusCode()).isEqualTo(200);
        assertThat(explicitResultFromOpaqueHelper.statusCode()).isEqualTo(200);
    }

    /**
     * Anti-pattern vs. fix for variable scope. {@code var items = new HashSet<>(...)} declared
     * far above its usage leaves the reader seeing only {@code items}, without the hint that
     * iteration order is not insertion order. Declaring the variable next to its usage, with a
     * collection whose semantics match the intent ({@link ArrayList} keeps insertion order),
     * keeps the behavior visible.
     */
    @Test
    void scopeLeakage_JuniorTrap_Vs_SeniorTightScope() {
        // JUNIOR TRAP: Declaring 'var items = new HashSet<>(...)' far above usage
        // hides the loss of insertion order.
        var juniorItems = new HashSet<>(fetchIds());
        juniorItems.add("FINAL_AUDIT_ORDER");

        // SENIOR PATTERN (Guideline G2 / Effective Java Item 57):
        // Declare local variables immediately adjacent to their usage with clear collection semantics.
        var orderedAuditIds = new ArrayList<String>(fetchIds());
        orderedAuditIds.add("FINAL_AUDIT_ORDER");

        assertThat(orderedAuditIds).endsWith("FINAL_AUDIT_ORDER");
    }
}
