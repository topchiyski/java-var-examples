package com.acme.var;

/**
 * Pillar 1: {@code var} Is Not a Reserved Keyword (JLS §3.8, §3.9).
 * <p>
 * {@code var} is not a reserved keyword like {@code class} or {@code int}. Since Java 17,
 * JLS §3.9 classifies it as a <em>contextual keyword</em> (Java 10–16 called it a
 * <em>restricted identifier</em>): it has special meaning only where a local-variable type
 * is expected, and JLS §3.8 excludes it from {@code TypeIdentifier}, so no type may be
 * named {@code var}.
 * <p>
 * Therefore:
 * <ul>
 *   <li>Package names can contain {@code var} ({@code com.acme.var})</li>
 *   <li>Capitalized class names can be {@code Var} (identifiers are case-sensitive)</li>
 *   <li>Method names can be {@code var()}</li>
 *   <li>Local variable names and field names can be {@code var}</li>
 *   <li>However, {@code public class var {}} is a compile-time error</li>
 * </ul>
 */
public class Var {

    /**
     * A field legally named {@code var}.
     */
    private int var = 1;

    /**
     * Creates an example of the {@code var} contextual keyword used as an identifier.
     */
    public Var() {
    }

    /**
     * Returns the value of the field named {@code var}.
     * <p>
     * The method itself is also named {@code var}, and its body declares a local
     * variable named {@code var} whose type is inferred with {@code var}.
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
