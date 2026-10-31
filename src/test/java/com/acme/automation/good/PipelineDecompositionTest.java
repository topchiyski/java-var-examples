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
 * Long stream chains are often written as a single expression to avoid spelling out
 * verbose intermediate types. {@code var} removes that cost, so the chain can be split
 * into named steps that can be inspected in a debugger and asserted on individually.
 */
class PipelineDecompositionTest {

    /**
     * Returns a fixed audit log: three failures (two {@code ERR_GATEWAY_TIMEOUT}, one
     * {@code ERR_AUTH_EXPIRED}) and one success.
     *
     * @return the audit log entries
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
     * Anti-pattern: the whole computation is a single assertion argument. If it fails, the
     * message shows only the final {@code Optional}, not which step produced the wrong value.
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
     * Fix: each intermediate result gets a descriptive name via {@code var}, and the
     * frequency map gets its own described assertion, so a failure points to the step at fault.
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
