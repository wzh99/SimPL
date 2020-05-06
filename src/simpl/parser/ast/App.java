package simpl.parser.ast;

import simpl.interpreter.*;
import simpl.typing.*;

public class App extends BinaryExpr {

    public App(Expr l, Expr r) {
        super(l, r);
    }

    public String toString() {
        return "(" + l + " " + r + ")";
    }

    @Override public TypeResult typeCheck(TypeEnv E) throws TypeError {
        // Check types of function and argument
        var fnTr = l.typeCheck(E);
        var argTr = r.typeCheck(E);
        var subst = fnTr.s.compose(argTr.s);
        var fnTy = subst.apply(fnTr.t);
        var argTy = subst.apply(argTr.t);

        // Infer result type
        if (fnTy instanceof ArrowType) { // lhs known to be a function
            // Check conformance of parameter and argument
            var paramTy = ((ArrowType) fnTy).t1;
            subst = subst.compose(argTy.unify(paramTy));
            var resTy = subst.apply(((ArrowType) fnTy).t2);
            return TypeResult.of(subst, resTy);
        }
        else if (fnTy instanceof TypeVar) { // a type variable
            // Infer result type of function
            var resTv = new TypeVar(true);
            subst = subst.compose(fnTy.unify(new ArrowType(argTy, resTv)));
            var resTy = subst.apply(resTv);
            return TypeResult.of(subst, resTy);
        }
        throw new TypeError("lhs is not function type");
    }

    @Override public Value eval(State s) throws RuntimeError {
        var lhsVal = l.eval(s);
        if (!(lhsVal instanceof FunValue)) {
            throw new RuntimeError("lhs is not a function");
        }
        var fnVal = (FunValue) lhsVal;

        if (Feature.LAZY) {
            // Lazy evaluation
            return fnVal.e.eval(State.of(Env.of(fnVal.E, fnVal.x, r, s.E), s.M, s.p));
        }
        else {
            // Eager evaluation
            var argVal = r.eval(s);
            return fnVal.e.eval(State.of(Env.of(fnVal.E, fnVal.x, argVal), s.M, s.p));
        }
    }
}
