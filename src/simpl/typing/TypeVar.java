package simpl.typing;

import simpl.parser.Symbol;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class TypeVar extends Type {

    private static int typeVarCount = 0;

    private boolean equalityType;
    private Symbol name;

    public TypeVar(boolean equalityType) {
        this.equalityType = equalityType;
        name = Symbol.symbol("tv" + ++typeVarCount);
    }

    // Can only be used to clone type.
    private TypeVar(Symbol name, boolean equalityType) {
        this.name = name;
        this.equalityType = equalityType;
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
            // Replace this type variable with that type.
            return Substitution.of(this, other);
        }
    }

    @Override public Set<TypeVar> collect() {
        return new HashSet<>(Set.of(this));
    }

    @Override public Type clone() {
        return new TypeVar(name, equalityType);
    }

    public String toString() {
        return "" + name;
    }

    @Override public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        TypeVar typeVar = (TypeVar) o;
        return name.equals(typeVar.name);
    }

    @Override public int hashCode() {
        return Objects.hash(name);
    }

    @Override public boolean contains(TypeVar tv) {
        // Symbol with the same name will always be reference equal.
        return name == tv.name;
    }

    @Override public Type replace(TypeVar a, Type t) {
        return contains(a) ? t : this;
    }
}
