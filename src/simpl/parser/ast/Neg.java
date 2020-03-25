package simpl.parser.ast;

import simpl.interpreter.IntValue;
import simpl.interpreter.RuntimeError;
import simpl.interpreter.State;
import simpl.interpreter.Value;
import simpl.typing.Type;
import simpl.typing.TypeEnv;
import simpl.typing.TypeError;
import simpl.typing.TypeResult;

public class Neg extends UnaryExpr {

    public Neg(Expr e) {
        super(e);
    }

    public String toString() {
        return "~" + e;
    }

    @Override public TypeResult typeCheck(TypeEnv E) throws TypeError {
        var exprTr = e.typeCheck(E);
        var subst = exprTr.s.compose(exprTr.t.unify(Type.INT));
        return TypeResult.of(subst, Type.INT);
    }

    @Override public Value eval(State s) throws RuntimeError {
        var val = e.eval(s);
        if (!(val instanceof IntValue)) {
            throw new RuntimeError("not an integer");
        }
        return new IntValue(-((IntValue) val).n);
    }
}
