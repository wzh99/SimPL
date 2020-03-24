package simpl.parser.ast;

import simpl.interpreter.RuntimeError;
import simpl.interpreter.State;
import simpl.interpreter.Value;
import simpl.typing.*;

public class Nil extends Expr {

    public String toString() {
        return "nil";
    }

    @Override public TypeResult typeCheck(TypeEnv E) throws TypeError {
        var elemTv = new TypeVar(true);
        return TypeResult.of(new ListType(elemTv));
    }

    @Override public Value eval(State s) throws RuntimeError {
        return Value.NIL;
    }
}
