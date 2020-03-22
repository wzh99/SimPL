package simpl.typing;

import simpl.parser.Symbol;

public class TypeVar extends Type {

    private static int typeVarCount = 0;

    private boolean equalityType;
    private Symbol name;

    public TypeVar(boolean equalityType) {
        this.equalityType = equalityType;
        name = Symbol.symbol("tv" + ++typeVarCount);
    }

    @Override public boolean isEqualityType() {
        return equalityType;
    }

    @Override public Substitution unify(Type other) throws TypeCircularityError {
        if (other instanceof TypeVar && contains((TypeVar) other)) {
            // The same type variable, let it be there.
            return Substitution.IDENTITY;
        }
        else if (other.contains(this)) {
            // That type contain this type variable, there must be a type circularity.
            throw new TypeCircularityError();
        }
        else {
            // Replace this type variable with that concrete type..
            return Substitution.of(this, other);
        }
    }

    public String toString() {
        return "" + name;
    }

    @Override public boolean contains(TypeVar tv) {
        // Symbol with the same name will always be reference equal.
        return name == tv.name;
    }

    @Override public Type replace(TypeVar a, Type t) {
        return contains(a) ? t : this;
    }
}
