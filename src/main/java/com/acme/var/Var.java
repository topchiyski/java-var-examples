package com.acme.var;

public class Var {

    private int var = 1;

    public Var() {
    }

    public int var() {
        var var = this.var;
        return var;
    }

    public String inferredStringNamedVar() {
        var var = "var";
        return var;
    }
}
