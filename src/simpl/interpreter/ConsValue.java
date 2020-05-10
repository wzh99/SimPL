package simpl.interpreter;

public class ConsValue extends Value {

    public final Value v1, v2;

    public ConsValue(Value v1, Value v2) {
        this.v1 = v1;
        this.v2 = v2;
    }

    public String toString() {
//        return v1 + "::" + v2;
        return "list@" + len();
    }

    @Override public boolean equals(Object other) {
        if (!(other instanceof ConsValue))
            return false;
        var cons = (ConsValue) other;
        return v1.equals(cons.v1) && v2.equals(cons.v2);
    }

    private int len() {
        return v2 instanceof NilValue ? 1 : (((ConsValue) v2).len() + 1);
    }
}
