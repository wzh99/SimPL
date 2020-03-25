package simpl.parser.ast;

import simpl.interpreter.BoolValue;
import simpl.interpreter.RuntimeError;
import simpl.interpreter.State;
import simpl.interpreter.Value;
import simpl.typing.Type;
import simpl.typing.TypeEnv;
import simpl.typing.TypeError;
import simpl.typing.TypeResult;

public class Not extends UnaryExpr {

    public Not(Expr e) {
        super(e);
    }

    public String toString() {
        return "(not " + e + ")";
    }

    @Override public TypeResult typeCheck(TypeEnv E) throws TypeError {
        var exprTr = e.typeCheck(E);
        var subst = exprTr.s.compose(exprTr.t.unify(Type.BOOL));
        return TypeResult.of(subst, Type.BOOL);
    }

    @Override public Value eval(State s) throws RuntimeError {
        var val = e.eval(s);
        if (!(val instanceof BoolValue)) {
            throw new RuntimeError("not a boolean");
        }
        return new BoolValue(!((BoolValue) val).b);
    }
}
