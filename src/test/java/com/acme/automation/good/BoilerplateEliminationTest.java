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
 *
 * Contrasts Java 8 explicit manifest boilerplate against Java 10+ 'var' when
 * the right-hand initializer already provides explicit type context.
 */
class BoilerplateEliminationTest {

    private final WebDriver driver = new MockWebDriver();
    private final String authSpec = "Bearer token";

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
            .extract().as(new TypeRef<Map<String, List<Map<String, Object>>>>() {});

        WebElement euHeader = wait.until(
            visibilityOfElementLocated(By.cssSelector("[data-region='eu']"))
        );

        assertThat(opts).isNotNull();
        assertThat(euHeader.getText()).contains("EU Region");
        assertThat(ordersByRegion.get("EU")).isNotEmpty();
    }

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
            .extract().as(new TypeRef<Map<String, List<Map<String, Object>>>>() {});

        var euRegionHeaderElement = wait.until(
            visibilityOfElementLocated(By.cssSelector("[data-region='eu']"))
        );

        assertThat(opts).isNotNull();
        assertThat(euRegionHeaderElement.getText()).contains("EU Region");
        assertThat(ordersByRegion.get("EU")).isNotEmpty();
    }
}
