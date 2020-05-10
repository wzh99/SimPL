package simpl.typing;

import simpl.parser.Symbol;

public class DefaultTypeEnv extends TypeEnv {

    public DefaultTypeEnv() {
        // Create empty type environment as the starting point
        E = new TypeEnv();

        // Add type declarations for built-in functions
        var fstLhs = new TypeVar(true);
        var fstRhs = new TypeVar(true);
        E = ofGeneralized(E, Symbol.symbol("fst"), new ArrowType(new PairType(fstLhs, fstRhs), fstLhs));

        var sndLhs = new TypeVar(true);
        var sndRhs = new TypeVar(true);
        E = ofGeneralized(E, Symbol.symbol("snd"), new ArrowType(new PairType(sndLhs, sndRhs), sndRhs));

        var hdElem = new TypeVar(true);
        E = ofGeneralized(E, Symbol.symbol("hd"), new ArrowType(new ListType(hdElem), hdElem));

        var tlElem = new TypeVar(true);
        E = ofGeneralized(E, Symbol.symbol("tl"), new ArrowType(new ListType(tlElem), new ListType(tlElem)));

        E = of(E, Symbol.symbol("iszero"), new ArrowType(new IntType(), new BoolType()));

        E = of(E, Symbol.symbol("pred"), new ArrowType(new IntType(), new IntType()));

        E = of(E, Symbol.symbol("succ"), new ArrowType(new IntType(), new IntType()));

        var streamElem = new TypeVar(true);
        E = ofGeneralized(E, Symbol.symbol("stream"), new ArrowType(streamElem,
            new ArrowType(new ArrowType(Type.UNIT, new StreamType(streamElem)), new StreamType(streamElem))));
        E = ofGeneralized(E, Symbol.symbol("take"),
            new ArrowType(Type.INT, new ArrowType(new StreamType(streamElem), new ListType(streamElem))));
        E = ofGeneralized(E, Symbol.symbol("drop"),
            new ArrowType(Type.INT, new ArrowType(new StreamType(streamElem), new StreamType(streamElem))));
    }
}
