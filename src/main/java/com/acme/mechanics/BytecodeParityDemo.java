package com.acme.mechanics;

import java.util.List;

// var is resolved entirely by javac. The compiler infers each type from the initializer
// and writes it into the class file, so the JVM never sees "var" and there is no runtime
// cost or dynamic typing. Run: javap -v -c target/classes/com/acme/mechanics/BytecodeParityDemo.class
public class BytecodeParityDemo {

    public BytecodeParityDemo() {
    }

    public int computeTotalExplicit() {
        String prefix = "ORD-";
        List<Integer> prices = List.of(10, 20);
        int base = 5;
        return base + prices.size() + prefix.length();
    }

    // Same method body with var: javac infers String, List<Integer> and int, exactly the
    // types written above. Both methods emit identical instructions (ldc, astore_1,
    // astore_2, istore_3) and identical LocalVariableTable / LocalVariableTypeTable entries.
    // Only the LineNumberTable differs, because the code sits on different source lines.
    public int computeTotalInferred() {
        var prefix = "ORD-";
        // The generic signature List<Integer> is still recorded in LocalVariableTypeTable,
        // so debuggers and tools see the full type even though the source never spells it.
        var prices = List.of(10, 20);
        // An int literal infers the primitive int, not Integer: var never boxes.
        var base = 5;
        return base + prices.size() + prefix.length();
    }
}
