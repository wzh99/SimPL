package simpl.typing;

import java.util.Set;

public final class ListType extends Type {

    public Type t;

    public ListType(Type t) {
        this.t = t;
    }

    @Override public boolean isEqualityType() {
        return t.isEqualityType();
    }

    @Override public Substitution unify(Type other) throws TypeError {
        if (other instanceof TypeVar) {
            return other.unify(this);
        }
        else if (other instanceof ListType) {
            return t.unify(((ListType) other).t);
        }
        throw new TypeMismatchError(this, other);
    }

    @Override public Set<TypeVar> collect() {
        return t.collect();
    }

    @Override public Type clone() {
        return new ListType(t.clone());
    }

    @Override public boolean equals(Object other) {
        if (!(other instanceof ListType))
            return false;
        return t.equals(((ListType) other).t);
    }

    @Override public int hashCode() {
        return getClass().hashCode() ^ t.hashCode();
    }

    @Override public int height() {
        return 1 + t.height();
    }

    @Override public boolean contains(TypeVar tv) {
        return t.contains(tv);
    }

    @Override public Type replace(TypeVar a, Type t) {
        return new ListType(this.t.replace(a, t));
    }

    public String toString() {
        return t + " list";
    }
}
