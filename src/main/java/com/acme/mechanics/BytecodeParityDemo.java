package com.acme.mechanics;

import java.util.List;

public class BytecodeParityDemo {

    public BytecodeParityDemo() {
    }

    public int computeTotalExplicit() {
        String prefix = "ORD-";
        List<Integer> prices = List.of(10, 20);
        int base = 5;
        return base + prices.size() + prefix.length();
    }

    public int computeTotalInferred() {
        var prefix = "ORD-";
        var prices = List.of(10, 20);
        var base = 5;
        return base + prices.size() + prefix.length();
    }
}
