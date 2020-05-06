package simpl.typing;

import java.util.Set;

public final class RefType extends Type {

    public Type t;

    public RefType(Type t) {
        this.t = t;
    }

    @Override public boolean isEqualityType() {
        return true;
    }

    @Override public Substitution unify(Type other) throws TypeError {
        if (other instanceof TypeVar) {
            other.unify(this);
        }
        else if (other instanceof RefType) {
            t.unify(((RefType) other).t);
        }
        throw new TypeMismatchError(this, other);
    }

    @Override public Set<TypeVar> collect() {
        return t.collect();
    }

    @Override public Type clone() {
        return new RefType(t.clone());
    }

    @Override public boolean contains(TypeVar tv) {
        return false;
    }

    @Override public Type replace(TypeVar a, Type t) {
        return new RefType(this.t.replace(a, t));
    }

    public String toString() {
        return t + " ref";
    }
}
