package simpl.interpreter;

public class NilValue extends Value {

    public NilValue() {
    }

    public String toString() {
        return "nil";
    }

    @Override public boolean equals(Object other) {
        return other instanceof NilValue;
    }
}
