package com.acme.var;

/**
 * Pillar 1: {@code var} Is Not a Reserved Keyword (JLS §3.8, §3.9).
 * <p>
 * {@code var} is a <em>contextual keyword</em> (earlier JLS editions said "restricted identifier"):
 * it can name packages, methods, fields, and variables, but not types. {@code Var} is fine
 * because identifiers are case-sensitive; {@code class var {}} does not compile.
 *
 * @see <a href="https://www.linkedin.com/pulse/why-your-java-var-code-fails-review-stoyan-topchiyski-xwome">Why Your Java var Code Fails Code Review (LinkedIn)</a>
 */
public class Var {

    /**
     * A field legally named {@code var}.
     */
    private int var = 1;

    /**
     * Creates the example.
     */
    public Var() {
    }

    /**
     * A method named {@code var} that reads the field into a local {@code var var}.
     *
     * @return the field value
     */
    public int var() {
        var var = this.var;
        return var;
    }

    /**
     * Uses {@code var} as both the type and the name of a local variable.
     *
     * @return {@code "var"}
     */
    public String inferredStringNamedVar() {
        var var = "var";
        return var;
    }
}
