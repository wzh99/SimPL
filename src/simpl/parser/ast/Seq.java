package simpl.parser.ast;

import simpl.interpreter.RuntimeError;
import simpl.interpreter.State;
import simpl.interpreter.Value;
import simpl.typing.TypeEnv;
import simpl.typing.TypeError;
import simpl.typing.TypeResult;

public class Seq extends BinaryExpr {

    public Seq(Expr l, Expr r) {
        super(l, r);
    }

    public String toString() {
        return "(" + l + " ; " + r + ")";
    }

    @Override public TypeResult typeCheck(TypeEnv E) throws TypeError {
        // Check lhs statement
        var lhsTy = l.typeCheck(E);
        // Check rhs statement
        var rhsTy = r.typeCheck(E);
        // Return type of rhs
        var subst = lhsTy.s.compose(rhsTy.s);
        return TypeResult.of(subst, subst.apply(rhsTy.t));
    }

    @Override public Value eval(State s) throws RuntimeError {
        l.eval(s);
        return r.eval(s);
    }
}
