package com.acme.automation.good;

import com.acme.automation.support.TestMocks.*;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.List;
import java.util.Map;

import static com.acme.automation.support.TestMocks.given;
import static com.acme.automation.support.TestMocks.visibilityOfElementLocated;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Pillar 2 (The Good): Eliminating Boilerplate (Guideline G3).
 * <p>
 * Contrasts Java 8 explicit manifest boilerplate against Java 10+ {@code var} when
 * the right-hand initializer already provides explicit type context: a constructor call,
 * a typed factory, or a type token such as {@link TypeRef}.
 * <p>
 * Both tests perform the same Selenium and RestAssured steps and assertions; only the
 * local-variable declarations differ.
 */
class BoilerplateEliminationTest {

    /** Driver used for the UI part of both tests. */
    private final WebDriver driver = new MockWebDriver();

    /** Authorization spec passed to the API request. */
    private final String authSpec = "Bearer token";

    /**
     * Before: every local variable repeats a type that is already visible on the
     * right-hand side, including a nested generic type that is written out twice.
     */
    @Test
    void verifyOrdersExplicit_Before() {
        ChromeOptions opts = new ChromeOptions();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // Nested generic noise obscures the variable name
        Map<String, List<Map<String, Object>>> ordersByRegion = given()
                .spec(authSpec)
                .when()
                .get("/api/v2/orders/summary")
                .then().statusCode(200)
                .extract().as(new TypeRef<Map<String, List<Map<String, Object>>>>() {
                });

        WebElement euHeader = wait.until(
                visibilityOfElementLocated(By.cssSelector("[data-region='eu']"))
        );

        assertThat(opts).isNotNull();
        assertThat(euHeader.getText()).contains("EU Region");
        assertThat(ordersByRegion.get("EU")).isNotEmpty();
    }

    /**
     * After: {@code var} removes the duplicated types, so the domain variable names line up
     * and stand out. Each type is still visible on the right-hand side.
     */
    @Test
    void verifyOrdersInferred_After() {
        var opts = new ChromeOptions();
        var wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // Domain variable name stands out cleanly while TypeRef supplies the exact type
        var ordersByRegion = given()
                .spec(authSpec)
                .when()
                .get("/api/v2/orders/summary")
                .then().statusCode(200)
                .extract().as(new TypeRef<Map<String, List<Map<String, Object>>>>() {
                });

        var euRegionHeaderElement = wait.until(
                visibilityOfElementLocated(By.cssSelector("[data-region='eu']"))
        );

        assertThat(opts).isNotNull();
        assertThat(euRegionHeaderElement.getText()).contains("EU Region");
        assertThat(ordersByRegion.get("EU")).isNotEmpty();
    }
}
