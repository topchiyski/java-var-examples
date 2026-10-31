package com.acme.automation.ugly;

import org.junit.jupiter.api.Test;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Pillar 2 (The Ugly - Edge Case 3B): Non-Denotable Intersection Types in Heterogeneous Data.
 * <p>
 * When a generic factory receives arguments of different types and has no target type,
 * javac infers their Least Upper Bound (LUB): an intersection of every supertype they share.
 * {@code var} preserves this intersection type, which cannot be written in source code and
 * rejects elements that do not implement every interface in it.
 */
class IntersectionTypeTrapTest {

    /**
     * Trap: {@code List.of("hello", 1, 2.0)} infers an intersection element type, and an
     * {@link ArrayList} copied from it inherits that type. A list meant to collect arbitrary
     * fuzz inputs therefore rejects a {@link Map} or a plain {@link Object} at compile time.
     */
    @Test
    void heterogeneousList_InferredIntersectionType() {
        // Without a left-hand target type, javac computes the Least Upper Bound (LUB)
        // of String, Integer, and Double. On JDK 12+ that is:
        // List<Serializable & Comparable<? extends ...> & Constable & ConstantDesc>
        var inferredPayloads = List.of("hello", 1, 2.0);

        var firstElement = inferredPayloads.get(0);
        assertThat(firstElement)
                .isInstanceOf(Serializable.class)
                .isInstanceOf(Comparable.class);

        // JUNIOR TRAP: Wrapping that inferred list in an ArrayList prevents adding Map or Object later!
        var trappedFuzzInputs = new ArrayList<>(List.of("hello", 1, 2.0));
        // trappedFuzzInputs.add(Map.of("malformed", true)); // <-- COMPILE ERROR: Map is not Comparable!
        // trappedFuzzInputs.add(new Object());              // <-- COMPILE ERROR!
        assertThat(trappedFuzzInputs).hasSize(3);
    }

    /**
     * Fix: an explicit {@code <Object>} type witness on {@code List.of} replaces the inferred
     * intersection type, so the copied list accepts any element.
     */
    @Test
    void heterogeneousFuzzData_SeniorPattern_ExplicitTypeWitness() {
        // SENIOR PATTERN: Provide an explicit <Object> type witness on the right-hand factory
        // so 'var' infers ArrayList<Object>.
        var safeFuzzInputs = new ArrayList<>(List.<Object>of("hello", 1, 2.0));

        safeFuzzInputs.add(Map.of("malformed", true));
        safeFuzzInputs.add(new Object());

        assertThat(safeFuzzInputs).hasSize(5);
    }
}
