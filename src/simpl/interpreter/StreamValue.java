package simpl.interpreter;

public class StreamValue extends Value {

    public final Value x;
    public final FunValue f;

    public StreamValue(Value x, FunValue f) {
        this.x = x;
        this.f = f;
    }

    @Override public boolean equals(Object other) {
        return false;
    }
}
