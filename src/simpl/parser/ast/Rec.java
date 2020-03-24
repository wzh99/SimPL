package simpl.parser.ast;

import simpl.interpreter.*;
import simpl.parser.Symbol;
import simpl.typing.TypeEnv;
import simpl.typing.TypeError;
import simpl.typing.TypeResult;
import simpl.typing.TypeVar;

public class Rec extends Expr {

    public Symbol x;
    public Expr e;

    public Rec(Symbol x, Expr e) {
        this.x = x;
        this.e = e;
    }

    public String toString() {
        return "(rec " + x + "." + e + ")";
    }

    @Override public TypeResult typeCheck(TypeEnv E) throws TypeError {
        // Define recursive type
        var recTv = new TypeVar(false);

        // Infer type in recursive expression
        var resTr = e.typeCheck(TypeEnv.of(E, x, recTv));
        var resTy = resTr.t;

        // Unify recursive and result type
        var subst = resTr.s.compose(recTv.unify(resTy));
        resTy = subst.apply(resTy);

        // Return typing result. Result type is chosen because it usually contains more concrete
        // types.
        return TypeResult.of(subst, resTy);
    }

    @Override public Value eval(State s) throws RuntimeError {
        // Evaluate expression, add recursive function name to environment.
        return e.eval(State.of(Env.of(s.E, x, new RecValue(s.E, x, e)), s.M, s.p));
    }
}
