package simpl.interpreter.lib;

import simpl.interpreter.*;
import simpl.parser.Symbol;
import simpl.parser.ast.Expr;
import simpl.typing.TypeEnv;
import simpl.typing.TypeResult;

public class Tl extends FunValue {

    public Tl() {
        super(Env.empty, Symbol.symbol("x"), new Expr() {
            Symbol x = Symbol.symbol("x");

            @Override public TypeResult typeCheck(TypeEnv E) {
                return null;
            }

            @Override public Value eval(State s) throws RuntimeError {
                var listVal = s.E.get(x, s);
                if (!(listVal instanceof ConsValue)) {
                    throw new RuntimeError("not cons");
                }
                return ((ConsValue) listVal).v2;
            }
        });
    }
}
