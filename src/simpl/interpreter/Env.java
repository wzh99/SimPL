package simpl.interpreter;

import simpl.parser.Symbol;
import simpl.parser.ast.Expr;

public class Env {

    public final Env E;
    private final Symbol x;
    public final Value v;
    private final Expr e;
    private final Env Ee;

    private Env() {
        E = null;
        x = null;
        v = null;
        e = null;
        Ee = null;
    }

    public static Env empty = new Env();

    // Eager evaluation (call by value)
    public Env(Env E, Symbol x, Value v) {
        this.E = E;
        this.x = x;
        this.v = v;
        this.e = null;
        this.Ee = null;
    }

    // Lazy evaluation (call by need)
    public Env(Env E, Symbol x, Expr e, Env Ee) {
        this.E = E;
        this.x = x;
        this.v = null;
        this.e = e;
        this.Ee = Ee;
    }

    public static Env of(Env E, Symbol x, Value v) {
        return new Env(E, x, v);
    }

    public static Env of(Env E, Symbol x, Expr e, Env Ee) {
        return new Env(E, x, e, Ee);
    }

    public Value get(Symbol y, State s) throws RuntimeError {
        assert E != null;
        if (x != y)
            return E.get(y, s);
        if (v == null) {
            assert e != null;
            return e.eval(State.of(Ee, s.M, s.p));
        }
        else {
            return v;
        }
    }

    public Env clone() {
        return new Env(E, x, v);
    }
}
