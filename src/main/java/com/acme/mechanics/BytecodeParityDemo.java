package com.acme.mechanics;

import java.util.List;

/**
 * Pillar 1: Bytecode Parity Proof.
 * <p>
 * {@code var} is resolved entirely at compile time: the inferred type is written to the
 * class file exactly as if it had been declared explicitly. There is no runtime cost and
 * no dynamic typing.
 * <p>
 * Compile with debug info ({@code javac -g}; Maven does this by default) and inspect with:
 * <pre>{@code javap -v -c BytecodeParityDemo.class}</pre>
 * <p>
 * Both methods produce identical bytecode instructions ({@code ldc}, {@code astore_1},
 * {@code astore_2}, {@code istore_3}) and identical {@code LocalVariableTable} /
 * {@code LocalVariableTypeTable} descriptors. Only the {@code LineNumberTable} differs.
 */
public class BytecodeParityDemo {

    /**
     * Creates a bytecode parity example.
     */
    public BytecodeParityDemo() {
    }

    /**
     * Computes a total using explicit local-variable types.
     * <p>
     * Reference implementation for {@link #computeTotalInferred()}.
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
     * <p>
     * Compiles to the same bytecode as {@link #computeTotalExplicit()}.
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
