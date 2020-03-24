package simpl.typing;

import simpl.parser.Symbol;

public class DefaultTypeEnv extends TypeEnv {

    private TypeEnv E;

    public DefaultTypeEnv() {
        // Create empty type environment as the starting point
        E = empty;

        // Add type declarations for built-in functions
        var fstLhs = new TypeVar(true);
        var fstRhs = new TypeVar(true);
        E = of(E, Symbol.symbol("fst"), new ArrowType(new PairType(fstLhs, fstRhs), fstLhs));

        var sndLhs = new TypeVar(true);
        var sndRhs = new TypeVar(true);
        E = of(E, Symbol.symbol("snd"), new ArrowType(new PairType(sndLhs, sndRhs), sndRhs));

        var hdElem = new TypeVar(true);
        E = of(E, Symbol.symbol("hd"), new ArrowType(new ListType(hdElem), hdElem));

        var tlElem = new TypeVar(true);
        E = of(E, Symbol.symbol("tl"), new ArrowType(new ListType(tlElem), new ListType(tlElem)));

        E = of(E, Symbol.symbol("iszero"), new ArrowType(new IntType(), new BoolType()));

        E = of(E, Symbol.symbol("pred"), new ArrowType(new IntType(), new IntType()));

        E = of(E, Symbol.symbol("succ"), new ArrowType(new IntType(), new IntType()));
    }

    @Override public Type get(Symbol x) {
        return E.get(x);
    }
}
