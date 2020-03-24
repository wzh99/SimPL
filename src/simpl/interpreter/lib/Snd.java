package simpl.interpreter.lib;

import simpl.interpreter.*;
import simpl.parser.Symbol;
import simpl.parser.ast.Expr;
import simpl.typing.TypeEnv;
import simpl.typing.TypeResult;

public class Snd extends FunValue {

    public Snd() {
        // TODO
        super(Env.empty, Symbol.symbol("x"), new Expr() {
            Symbol x = Symbol.symbol("x");

            @Override public TypeResult typeCheck(TypeEnv E) {
                return null;
            }

            @Override public Value eval(State s) throws RuntimeError {
                var pairVal = s.E.get(x);
                if (!(pairVal instanceof PairValue)) {
                    throw new RuntimeError("not a pair");
                }
                return ((PairValue) pairVal).v2;
            }
        });
    }
}
