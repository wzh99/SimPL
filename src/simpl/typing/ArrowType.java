package simpl.typing;

import java.util.HashSet;
import java.util.Set;

public final class ArrowType extends Type {

    public Type t1, t2;

    public ArrowType(Type t1, Type t2) {
        this.t1 = t1;
        this.t2 = t2;
    }

    @Override public boolean isEqualityType() {
        return false;
    }

    @Override public Substitution unify(Type other) throws TypeError {
        if (other instanceof TypeVar) {
            return other.unify(this);
        }
        else if (other instanceof ArrowType) {
            return t1.unify(((ArrowType) other).t1).compose(t2.unify(((ArrowType) other).t2));
        }
        throw new TypeMismatchError(this, other);
    }

    @Override public Set<TypeVar> collect() {
        var list = new HashSet<TypeVar>();
        list.addAll(t1.collect());
        list.addAll(t2.collect());
        return list;
    }

    @Override public Type clone() {
        return new ArrowType(t1.clone(), t2.clone());
    }

    @Override public boolean equals(Object other) {
        if (!(other instanceof ArrowType))
            return false;
        return t1.equals(((ArrowType) other).t1) && t2.equals(((ArrowType) other).t2);
    }

    @Override public int hashCode() {
        return getClass().hashCode() ^ t1.hashCode() ^ t2.hashCode();
    }

    @Override public int height() {
        return 1 + Math.max(t1.height(), +t2.height());
    }

    @Override public boolean contains(TypeVar tv) {
        return t1.contains(tv) || t2.contains(tv);
    }

    @Override public Type replace(TypeVar a, Type t) {
        return new ArrowType(t1.replace(a, t), t2.replace(a, t));
    }

    public String toString() {
        return "(" + t1 + " -> " + t2 + ")";
    }
}
