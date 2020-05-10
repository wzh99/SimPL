package simpl.interpreter.lib;

import simpl.interpreter.*;
import simpl.parser.Symbol;
import simpl.parser.ast.Expr;
import simpl.typing.TypeEnv;
import simpl.typing.TypeError;
import simpl.typing.TypeResult;

public class Drop extends FunValue {
    public Drop() {
        super(Env.empty, Symbol.symbol("n"), new Expr() {
            Symbol n = Symbol.symbol("n");

            @Override public TypeResult typeCheck(TypeEnv E) {
                return null;
            }

            @Override public Value eval(State s) throws RuntimeError {
                // Check number argument
                var numVal = s.E.get(n, s);
                if (!(numVal instanceof IntValue)) {
                    throw new RuntimeError("not an integer");
                }

                return new FunValue(s.E, Symbol.symbol("s"), new Expr() {
                    Symbol s = Symbol.symbol("s");

                    @Override public TypeResult typeCheck(TypeEnv E) throws TypeError {
                        return null;
                    }

                    @Override public Value eval(State state) throws RuntimeError {
                        // Check stream argument
                        var streamVal = state.E.get(s, state);
                        if (!(streamVal instanceof StreamValue)) {
                            throw new RuntimeError("not a stream");
                        }

                        // Drop certain number of elements
                        var curStream = (StreamValue) streamVal;
                        for (int i = 0; i < ((IntValue) numVal).n; i++) {
                            var funcVal = curStream.f;
                            var nextStream = funcVal.e.eval(State.of(funcVal.E, state.M, state.p));
                            if (!(nextStream instanceof StreamValue))
                                throw new RuntimeError("not a stream");
                            curStream = (StreamValue) nextStream;
                        }

                        return curStream;
                    }
                });
            }
        });
    }
}
