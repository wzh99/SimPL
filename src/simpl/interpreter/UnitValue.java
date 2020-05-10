package simpl.interpreter;

public class UnitValue extends Value {

    public UnitValue() {
    }

    public String toString() {
        return "unit";
    }

    @Override public boolean equals(Object other) {
        return other instanceof UnitValue;
    }
}
