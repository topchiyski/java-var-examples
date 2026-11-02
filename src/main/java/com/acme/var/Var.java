package com.acme.var;

// var is a contextual keyword (JLS §3.9), not a reserved keyword like int or class.
// It has special meaning only where a local variable's type is expected, so code written
// before Java 10 that used "var" as a name still compiles. The one thing it cannot name
// is a type: "class var {}" is a compile error. This class is legal because identifiers
// are case-sensitive, so Var is not var. Even the package name, com.acme.var, is legal.
public class Var {

    // A field named var: legal, because this is a name position, not a type position.
    private int var = 1;

    public Var() {
    }

    // A method named var: also legal, for the same reason.
    public int var() {
        // The first var is the type position (infer int), the second is the variable name.
        // this.var disambiguates the field from the local that shadows it.
        var var = this.var;
        return var;
    }

    public String inferredStringNamedVar() {
        // The parser tells the two apart by position: "var <identifier> =" declares an
        // inferred local, so here javac infers String for a variable called var.
        var var = "var";
        return var;
    }
}
