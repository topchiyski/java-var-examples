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
 * Use {@code var} when the right-hand side or the variable name already shows the type.
 * Both tests do the same work; only the declarations differ.
 */
class BoilerplateEliminationTest {

    /**
     * Driver for the UI steps.
     */
    private final WebDriver driver = new MockWebDriver();

    /**
     * Authorization spec for the API request.
     */
    private final String authSpec = "Bearer token";

    /**
     * Before: explicit types everywhere, including a nested generic type written twice.
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
     * After: {@code var} removes the duplication, so the variable names stand out.
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
