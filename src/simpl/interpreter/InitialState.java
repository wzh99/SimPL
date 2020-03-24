package simpl.interpreter;

import simpl.interpreter.lib.Fst;
import simpl.interpreter.lib.Hd;
import simpl.interpreter.lib.Snd;
import simpl.interpreter.lib.Tl;
import simpl.interpreter.pcf.IsZero;
import simpl.interpreter.pcf.Pred;
import simpl.interpreter.pcf.Succ;
import simpl.parser.Symbol;

public class InitialState extends State {

    public InitialState() {
        super(initialEnv(Env.empty), new Mem(), new Int(0));
    }

    private static Env initialEnv(Env E) {
        // Add built-in functions
        E = Env.of(E, Symbol.symbol("fst"), new Fst());
        E = Env.of(E, Symbol.symbol("snd"), new Snd());
        E = Env.of(E, Symbol.symbol("hd"), new Hd());
        E = Env.of(E, Symbol.symbol("tl"), new Tl());
        E = Env.of(E, Symbol.symbol("iszero"), new IsZero());
        E = Env.of(E, Symbol.symbol("pred"), new Pred());
        E = Env.of(E, Symbol.symbol("succ"), new Succ());
        return E;
    }
}
