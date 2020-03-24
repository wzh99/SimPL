package simpl.interpreter.pcf;

import simpl.interpreter.*;
import simpl.parser.Symbol;
import simpl.parser.ast.Expr;
import simpl.typing.TypeEnv;
import simpl.typing.TypeResult;

public class Pred extends FunValue {

    public Pred() {
        super(Env.empty, Symbol.symbol("x"), new Expr() {
            Symbol x = Symbol.symbol("x");

            @Override public TypeResult typeCheck(TypeEnv E) {
                return null;
            }

            @Override public Value eval(State s) throws RuntimeError {
                var intVal = s.E.get(x);
                if (!(intVal instanceof IntValue)) {
                    throw new RuntimeError("not an integer");
                }
                var val = ((IntValue) intVal).n;
                return new IntValue(val == 0 ? 0 : val - 1);
            }
        });
    }
}
