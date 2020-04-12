package simpl.parser.ast;

import simpl.interpreter.FunValue;
import simpl.interpreter.RuntimeError;
import simpl.interpreter.State;
import simpl.interpreter.Value;
import simpl.parser.Symbol;
import simpl.typing.*;

public class Fn extends Expr {

    public Symbol x;
    public Expr e;

    public Fn(Symbol x, Expr e) {
        this.x = x;
        this.e = e;
    }

    public String toString() {
        return "(fn " + x + "." + e + ")";
    }

    @Override public TypeResult typeCheck(TypeEnv E) throws TypeError {
        // Define parameter type as type variable
        var paramTv = new TypeVar(true);

        // Infer result type in expression
        var resTr = e.typeCheck(TypeEnv.of(E, x, paramTv));
        var resTy = resTr.t;
        var subst = resTr.s;

        // Substitute possibly concrete type for type variable
        var paramTy = subst.apply(paramTv);

        // Return type result
        return TypeResult.of(subst, new ArrowType(paramTy, resTy));
    }

    @Override public Value eval(State s) throws RuntimeError {
        // Store the closure
        return new FunValue(s.E, x, e);
    }
}
