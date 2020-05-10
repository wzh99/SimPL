package simpl.interpreter.lib;

import simpl.interpreter.*;
import simpl.parser.Symbol;
import simpl.parser.ast.Expr;
import simpl.typing.TypeEnv;
import simpl.typing.TypeResult;

public class Stream extends FunValue {
    public Stream() {
        super(Env.empty, Symbol.symbol("x"), new Expr() {
            Symbol x = Symbol.symbol("x");

            @Override public TypeResult typeCheck(TypeEnv E) {
                return null;
            }

            @Override public Value eval(State s) throws RuntimeError {
                var elemVal = s.E.get(x, s);
                return new FunValue(s.E, Symbol.symbol("f"), new Expr() {
                    Symbol f = Symbol.symbol("f");

                    @Override public TypeResult typeCheck(TypeEnv E) {
                        return null;
                    }

                    @Override public Value eval(State s) throws RuntimeError {
                        var funVal = s.E.get(f, s);
                        if (!(funVal instanceof FunValue)) {
                            throw new RuntimeError("not a function");
                        }
                        return new StreamValue(elemVal, (FunValue) funVal);
                    }
                });
            }
        });
    }
}
