package simpl.typing;

import java.util.Set;

public class StreamType extends Type {

    public Type t;

    public StreamType(Type t) {
        this.t = t;
    }

    @Override public boolean isEqualityType() {
        return t.isEqualityType();
    }

    @Override public Type replace(TypeVar a, Type t) {
        return new StreamType(this.t.replace(a, t));
    }

    @Override public boolean contains(TypeVar tv) {
        return t.contains(tv);
    }

    @Override public Substitution unify(Type other) throws TypeError {
        if (other instanceof TypeVar) {
            return other.unify(this);
        }
        else if (other instanceof StreamType) {
            return t.unify(((StreamType) other).t);
        }
        throw new TypeMismatchError(this, other);
    }

    @Override public Set<TypeVar> collect() {
        return t.collect();
    }

    @Override public Type clone() {
        return new StreamType(t.clone());
    }

    public String toString() {
        return t + " stream";
    }
}
