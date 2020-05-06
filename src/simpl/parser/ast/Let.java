package simpl.parser.ast;

import simpl.interpreter.*;
import simpl.parser.Symbol;
import simpl.typing.TypeEnv;
import simpl.typing.TypeError;
import simpl.typing.TypeResult;

public class Let extends Expr {

    public Symbol x;
    public Expr e1, e2;

    public Let(Symbol x, Expr e1, Expr e2) {
        this.x = x;
        this.e1 = e1;
        this.e2 = e2;
    }

    public String toString() {
        return "(let " + x + " = " + e1 + " in " + e2 + ")";
    }

    @Override public TypeResult typeCheck(TypeEnv E) throws TypeError {
        var tr1 = e1.typeCheck(E);
        var tr2 = e2.typeCheck(TypeEnv.ofGeneralized(E, x, tr1.t));
        var subst = tr1.s.compose(tr2.s);
        return TypeResult.of(subst, subst.apply(tr2.t));
    }

    @Override public Value eval(State s) throws RuntimeError {
        if (Feature.LAZY) {
            // Lazy evaluation
            return e2.eval(State.of(Env.of(s.E, x, e1, s.E), s.M, s.p));
        }
        else {
            // Eager evaluation
            var v1 = e1.eval(s);
            return e2.eval(State.of(Env.of(s.E, x, v1), s.M, s.p));
        }
    }
}
