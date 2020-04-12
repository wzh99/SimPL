package simpl.parser.ast;

import simpl.interpreter.*;
import simpl.parser.Symbol;
import simpl.typing.*;

// Extend `let` syntax to support mutually recursive combinator
public class LetAnd extends Expr {

    public Symbol x, y;
    public Expr e1, e2, e3;

    public LetAnd(Symbol x, Expr e1, Symbol y, Expr e2, Expr e3) {
        this.x = x;
        this.y = y;
        this.e1 = e1;
        this.e2 = e2;
        this.e3 = e3;
    }

    @Override public String toString() {
        return String.format("(let %s = %s and %s = %s in %s)", x, e1, y, e2, e3);
    }

    @Override public TypeResult typeCheck(TypeEnv E) throws TypeError {
        // Bind parametric function types to `x` and `y`
        var xTy = new ArrowType(new TypeVar(true), new TypeVar(true));
        var yTy = new ArrowType(new TypeVar(true), new TypeVar(true));

        // Check types of both recursive functions
        var env = TypeEnv.of(TypeEnv.of(E, x, xTy), y, yTy);
        var e1Tr = e1.typeCheck(env);
        var e2Tr = e2.typeCheck(env);
        var subst = e1Tr.s.compose(e2Tr.s);
        xTy = (ArrowType) subst.apply(xTy);
        yTy = (ArrowType) subst.apply(yTy);

        // Check type of expression
        var e3Tr = e3.typeCheck(TypeEnv.of(TypeEnv.of(E, x, xTy), y, yTy));
        subst.compose(e3Tr.s);
        return TypeResult.of(subst, subst.apply(e3Tr.t));
    }

    @Override public Value eval(State s) throws RuntimeError {
        if (EvalMode.LAZY)
            throw new RuntimeError("cannot evaluate in lazy mode");

        // Evaluate either function without binding
        var v1 = e1.eval(s);
        if (!(v1 instanceof FunValue))
            throw new RuntimeError("v1 is not a function");
        var v2 = e2.eval(s);
        if (!(v2 instanceof FunValue))
            throw new RuntimeError("v2 is not a function");

        // Modify closures to create correct name-value binding
        var env = Env.of(Env.of(s.E, x, v1), y, v2);
        ((FunValue) v1).E = env;
        ((FunValue) v2).E = env;

        // Evaluate the rest
        return e3.eval(State.of(env, s.M, s.p));
    }
}
