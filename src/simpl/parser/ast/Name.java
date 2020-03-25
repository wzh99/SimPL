package simpl.parser.ast;

import simpl.interpreter.RecValue;
import simpl.interpreter.RuntimeError;
import simpl.interpreter.State;
import simpl.interpreter.Value;
import simpl.parser.Symbol;
import simpl.typing.TypeEnv;
import simpl.typing.TypeError;
import simpl.typing.TypeResult;

public class Name extends Expr {

    public Symbol x;

    public Name(Symbol x) {
        this.x = x;
    }

    public String toString() {
        return "" + x;
    }

    @Override public TypeResult typeCheck(TypeEnv E) throws TypeError {
        // Get type from type environment
        var ty = E.get(x);
        if (ty == null) {
            throw new TypeError(String.format("symbol %s not found", x));
        }
        return TypeResult.of(ty);
    }

    @Override public Value eval(State s) throws RuntimeError {
        // Get value from environment
        var val = s.E.get(x, s);
        if (val == null) {
            throw new RuntimeError(String.format("symbol %s not found", x));
        }
        // Further evaluate if the name is bound to recursive function
        if (val instanceof RecValue) {
            var rec = new Rec(x, ((RecValue) val).e);
            // Restore environment recorded by `RecValue`
            return rec.eval(State.of(((RecValue) val).E, s.M, s.p));
        }
        return val;
    }
}
