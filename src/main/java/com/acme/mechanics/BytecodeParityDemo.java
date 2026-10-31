package com.acme.mechanics;

import java.util.List;

/**
 * Pillar 1: Bytecode Parity Proof.
 * <p>
 * {@code var} is resolved at compile time, so both methods compile to identical bytecode
 * and local-variable tables. Verify with {@code javap -v -c BytecodeParityDemo.class}.
 */
public class BytecodeParityDemo {

    /**
     * Creates a bytecode parity example.
     */
    public BytecodeParityDemo() {
    }

    /**
     * Computes the total with explicit types.
     *
     * @return base + number of prices + prefix length
     */
    public int computeTotalExplicit() {
        String prefix = "ORD-";
        List<Integer> prices = List.of(10, 20);
        int base = 5;
        return base + prices.size() + prefix.length();
    }

    /**
     * Computes the same total with {@code var}.
     *
     * @return base + number of prices + prefix length
     */
    public int computeTotalInferred() {
        var prefix = "ORD-";
        var prices = List.of(10, 20);
        var base = 5;
        return base + prices.size() + prefix.length();
    }
}
