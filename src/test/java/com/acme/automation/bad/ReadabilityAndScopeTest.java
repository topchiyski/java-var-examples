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
 * With {@code var}, names must carry the type information, or code becomes unreadable
 * outside an IDE, such as in a pull request diff.
 */
class ReadabilityAndScopeTest {

    // Opaque helper names (anti-pattern)

    /**
     * Opaque: the name hides that it returns a {@link By}.
     */
    private By get(String id) {
        return By.id(id);
    }

    /**
     * Opaque: the name hides that it returns a {@link WebElement}.
     */
    private WebElement find(By locator) {
        return new MockWebElement("button", "Submit");
    }

    /**
     * Opaque: the name hides what it processes and returns.
     */
    private OrderBatchResult processData(String id) {
        return new OrderBatchResult(200, id);
    }

    /**
     * Opaque, and without arguments there is nothing to hint at the result type.
     */
    private OrderBatchResult processData() {
        return new OrderBatchResult(200, "BATCH-1");
    }

    // Descriptive helper names (senior pattern)

    /**
     * Builds a locator for a test id.
     */
    private By buildTestIdLocator(String id) {
        return By.id(id);
    }

    /**
     * Waits until the located element is visible.
     */
    private WebElement waitForVisibleElement(By locator) {
        return new MockWebElement("button", "Submit");
    }

    /**
     * Submits the order with the given id.
     */
    private OrderBatchResult submitOrderById(String id) {
        return new OrderBatchResult(200, id);
    }

    /**
     * Returns the open modal dialog.
     */
    private WebElement getActiveModal() {
        return new MockWebElement("div", "Modal");
    }

    /**
     * Returns {@code "ORD-1"} and {@code "ORD-2"}, in that order.
     */
    private List<String> fetchIds() {
        return List.of("ORD-1", "ORD-2");
    }

    /**
     * Bad: short variable names plus opaque helpers leave every type unknown.
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
     * Good: type information comes from the variable name, the method name, or an explicit type.
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
     * Bad vs. good: declare {@code var} next to its usage, so the collection type and its
     * ordering stay visible.
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
