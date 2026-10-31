package com.acme.mechanics;

import java.util.List;

/**
 * Pillar 1: Bytecode Parity Proof.
 *
 * Compile with debug info (javac -g) and inspect with:
 * javap -v -c BytecodeParityDemo.class
 *
 * Both methods produce identical bytecode instructions (ldc, astore_1, astore_2, istore_3)
 * and identical LocalVariableTable / LocalVariableTypeTable descriptors.
 */
public class BytecodeParityDemo {

    /**
     * Creates a bytecode parity example.
     */
    public BytecodeParityDemo() {}

    /**
     * Computes a total using explicit local-variable types.
     *
     * @return the sum of the base value, number of prices, and prefix length
     */
    public int computeTotalExplicit() {
        String prefix = "ORD-";
        List<Integer> prices = List.of(10, 20);
        int base = 5;
        return base + prices.size() + prefix.length();
    }

    /**
     * Computes the same total using local-variable type inference.
     *
     * @return the sum of the base value, number of prices, and prefix length
     */
    public int computeTotalInferred() {
        var prefix = "ORD-";
        var prices = List.of(10, 20);
        var base = 5;
        return base + prices.size() + prefix.length();
    }
}
