package com.acme.var;

/**
 * Pillar 1: JLS §3.8 Restricted Identifier Demo.
 * <p>
 * Under Java Language Specification §3.8 and §3.9, 'var' is NOT a keyword;
 * it is a restricted identifier (TypeIdentifier).
 * <p>
 * Therefore:
 * - Package names can contain 'var' (com.acme.var)
 * - Capitalized class names can be 'Var' (case-sensitive)
 * - Method names can be 'var()'
 * - Local variable names and field names can be 'var'
 * - However, 'public class var {}' is a compile-time error.
 */
public class Var {

    private int var = 1;

    /**
     * Creates an example of the {@code var} restricted identifier.
     */
    public Var() {
    }

    /**
     * Returns the value of the field whose name is the restricted identifier {@code var}.
     *
     * @return the current field value
     */
    public int var() {
        var var = this.var;
        return var;
    }

    /**
     * Demonstrates using {@code var} as both a local-variable type and a variable name.
     *
     * @return the inferred string value
     */
    public String inferredStringNamedVar() {
        var var = "var";
        return var;
    }
}
