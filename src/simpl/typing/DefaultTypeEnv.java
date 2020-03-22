package simpl.typing;

import simpl.parser.Symbol;

public class DefaultTypeEnv extends TypeEnv {

    private TypeEnv E;

    public DefaultTypeEnv() {
        // Create empty type environment as the starting point
        E = empty;

        // Add entries for built-in functions
        // TODO
    }

    @Override public Type get(Symbol x) {
        return E.get(x);
    }
}
