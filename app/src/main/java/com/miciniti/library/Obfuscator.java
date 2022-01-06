package com.miciniti.library;

/**
 * Created by Miciniti on 18/05/2018.
 */

public class Obfuscator
{
    String value = "";

    public Obfuscator()
    {
        value = "";
    }

    public String test()
    {
        Obfuscator test = a().b().c();
        return test.value;
    }

    public Obfuscator join(String input)
    {
        this.value += input;
        return this;
    }

    public Obfuscator a() { return join("a"); }
    public Obfuscator b() { return join("b"); }
    public Obfuscator c() { return join("c"); }
    public Obfuscator d() { return join("d"); }
    public Obfuscator e() { return join("e"); }
    public Obfuscator f() { return join("f"); }
    public Obfuscator g() { return join("g"); }
    public Obfuscator h() { return join("h"); }
    public Obfuscator i() { return join("i"); }
    public Obfuscator j() { return join("j"); }
    public Obfuscator k() { return join("k"); }
    public Obfuscator l() { return join("l"); }
    public Obfuscator m() { return join("m"); }
    public Obfuscator n() { return join("n"); }
    public Obfuscator o() { return join("o"); }
    public Obfuscator p() { return join("p"); }
    public Obfuscator q() { return join("q"); }
    public Obfuscator r() { return join("r"); }
    public Obfuscator s() { return join("s"); }
    public Obfuscator t() { return join("t"); }
    public Obfuscator u() { return join("u"); }
    public Obfuscator v() { return join("v"); }
    public Obfuscator w() { return join("w"); }
    public Obfuscator x() { return join("x"); }
    public Obfuscator y() { return join("y"); }
    public Obfuscator z() { return join("z"); }
}
