package com.acme.automation.good;

import com.acme.automation.support.TestMocks.AuditLogDto;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Pillar 2 (The Good): Breaking Monolithic Chains into Diagnostic Checkpoints (Guideline G4).
 * <p>
 * {@code var} makes intermediate types cheap to declare, so long stream chains can be split
 * into named, debuggable steps.
 */
class PipelineDecompositionTest {

    /**
     * Returns three failures (two {@code ERR_GATEWAY_TIMEOUT}) and one success.
     */
    private List<AuditLogDto> fetchLogs() {
        return List.of(
                new AuditLogDto("FAILED", "ERR_GATEWAY_TIMEOUT"),
                new AuditLogDto("FAILED", "ERR_GATEWAY_TIMEOUT"),
                new AuditLogDto("FAILED", "ERR_AUTH_EXPIRED"),
                new AuditLogDto("SUCCESS", "NONE")
        );
    }

    /**
     * Bad: one monolithic chain; a failure doesn't show which step went wrong.
     */
    @Test
    void verifyTopError_JuniorTrap_MonolithicChain() {
        List<AuditLogDto> logs = fetchLogs();

        // JUNIOR TRAP: Chaining everything into a single statement just to avoid
        // declaring intermediate Map<String, Long> and Optional<Map.Entry<String, Long>> types.
        assertThat(
                logs.stream()
                        .filter(l -> "FAILED".equals(l.status()))
                        .collect(groupingBy(AuditLogDto::errorCode, counting()))
                        .entrySet().stream()
                        .max(Map.Entry.comparingByValue())
                        .map(Map.Entry::getKey)
        ).contains("ERR_GATEWAY_TIMEOUT");
    }

    /**
     * Good: named intermediate steps, each of which can be inspected and asserted on.
     */
    @Test
    void verifyTopError_SeniorPattern_NamedCheckpointsWithVar() {
        var auditLogs = fetchLogs();

        // Step 1: Inferred Map<String, Long> enables instant inspection & descriptive assertion
        var errorFrequencyByCode = auditLogs.stream()
                .filter(l -> "FAILED".equals(l.status()))
                .collect(groupingBy(AuditLogDto::errorCode, counting()));

        // Step 2: Inferred Optional<Map.Entry<String, Long>>
        var topErrorEntry = errorFrequencyByCode.entrySet().stream()
                .max(Map.Entry.comparingByValue());

        assertThat(errorFrequencyByCode)
                .as("Audit log failure frequency map")
                .containsKey("ERR_GATEWAY_TIMEOUT");

        assertThat(topErrorEntry.map(Map.Entry::getKey))
                .contains("ERR_GATEWAY_TIMEOUT");
    }
}
