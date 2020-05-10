package simpl.interpreter.lib;

import simpl.interpreter.*;
import simpl.parser.Symbol;
import simpl.parser.ast.Expr;
import simpl.typing.TypeEnv;
import simpl.typing.TypeError;
import simpl.typing.TypeResult;

import java.util.ArrayList;

public class Take extends FunValue {
    public Take() {
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

                        // Store elements in an array
                        var array = new ArrayList<Value>();
                        var curStream = (StreamValue) streamVal;
                        for (var i = 0; i < ((IntValue) numVal).n; i++) {
                            array.add(curStream.x);
                            var funcVal = curStream.f;
                            var nextStream = funcVal.e.eval(State.of(funcVal.E, state.M, state.p));
                            if (!(nextStream instanceof StreamValue))
                                throw new RuntimeError("not a stream");
                            curStream = (StreamValue) nextStream;
                        }

                        // Assemble list using cons in reverse direction
                        Value list = new NilValue();
                        for (var i = array.size() - 1; i >= 0; i--)
                            list = new ConsValue(array.get(i), list);
                        return list;
                    }
                });
            }
        });
    }
}
