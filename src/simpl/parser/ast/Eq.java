package simpl.parser.ast;

import simpl.interpreter.RuntimeError;
import simpl.interpreter.State;
import simpl.interpreter.Value;

public class Eq extends EqExpr {

    public Eq(Expr l, Expr r) {
        super(l, r);
    }

    public String toString() {
        return "(" + l + " = " + r + ")";
    }

    @Override public Value eval(State s) throws RuntimeError {
        // TODO
        return null;
    }
}
