package simpl.typing;

import simpl.interpreter.Feature;
import simpl.parser.Symbol;

public class TypeEnv {
    public TypeEnv E;
    public final Symbol x;
    public final Type t;

    TypeEnv(TypeEnv E, Symbol x, Type t) {
        this.E = E;
        this.x = x;
        this.t = t;
    }

    TypeEnv() {
        E = null;
        x = null;
        t = null;
    }

    public boolean isEmpty() {
        return x == null;
    }

    public Type get(Symbol x) {
        if (this.x == x)
            return t;
        else if (E != null)
            return E.get(x);
        else
            return null;
    }

    public static TypeEnv of(final TypeEnv E, final Symbol x, final Type t) {
        return new TypeEnv(E, x, t);
    }

    public static TypeEnv ofGeneralized(final TypeEnv E, final Symbol x, final Type t) {
        // Fall back to normal mapping if Let-Polymorphism is disabled
        if (!Feature.LET_POLY)
            return of(E, x, t);

        // Collect all type variables from given type
        var generalized = t.collect();

        // Prune variables that are already mentioned in typing environment
        // The rest are the ones to be generalized
        generalized.removeIf((TypeVar tv) -> {
            var curE = E;
            while (!curE.isEmpty()) {
                if (curE.t.contains(tv))
                    return true;
                curE = curE.E;
            }
            return false;
        });

        // Override get method of this entry
        return new TypeEnv(E, x, t) {
            @Override public Type get(Symbol x) {
                // Search inner entries if name does not match
                if (this.x != x) {
                    if (E != null)
                        return E.get(x);
                    else
                        return null;
                }

                // Instantiate all generalized type variables
                var ret = t.clone();
                for (var tv : generalized) {
                    var inst = new TypeVar(tv.isEqualityType());
                    ret = ret.replace(tv, inst);
                }
                return ret;
            }
        };
    }

    public String toString() {
        return x + ":" + t + ";" + E;
    }
}
