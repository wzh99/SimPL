package simpl.parser.ast;

import simpl.interpreter.*;

public class Less extends RelExpr {

    public Less(Expr l, Expr r) {
        super(l, r);
    }

    public String toString() {
        return "(" + l + " < " + r + ")";
    }

    @Override public Value eval(State s) throws RuntimeError {
        var lhsVal = l.eval(s);
        if (!(lhsVal instanceof IntValue)) {
            throw new RuntimeError("lhs is not integer");
        }
        var rhsVal = r.eval(s);
        if (!(rhsVal instanceof IntValue)) {
            throw new RuntimeError("rhs is not integer");
        }
        return new BoolValue(((IntValue) lhsVal).n < ((IntValue) rhsVal).n);
    }
}
