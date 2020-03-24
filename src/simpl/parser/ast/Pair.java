package simpl.parser.ast;

import simpl.interpreter.PairValue;
import simpl.interpreter.RuntimeError;
import simpl.interpreter.State;
import simpl.interpreter.Value;
import simpl.typing.PairType;
import simpl.typing.TypeEnv;
import simpl.typing.TypeError;
import simpl.typing.TypeResult;

public class Pair extends BinaryExpr {

    public Pair(Expr l, Expr r) {
        super(l, r);
    }

    public String toString() {
        return "(pair " + l + " " + r + ")";
    }

    @Override public TypeResult typeCheck(TypeEnv E) throws TypeError {
        var lhsTy = l.typeCheck(E);
        var rhsTy = r.typeCheck(E);
        var subst = lhsTy.s.compose(rhsTy.s);
        return TypeResult.of(subst, new PairType(subst.apply(lhsTy.t), subst.apply(rhsTy.t)));
    }

    @Override public Value eval(State s) throws RuntimeError {
        var lhsVal = l.eval(s);
        var rhsVal = r.eval(s);
        return new PairValue(lhsVal, rhsVal);
    }
}
