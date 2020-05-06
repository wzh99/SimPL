package simpl.typing;

import java.util.HashSet;
import java.util.Set;

public final class PairType extends Type {

    public Type t1, t2;

    public PairType(Type t1, Type t2) {
        this.t1 = t1;
        this.t2 = t2;
    }

    @Override public boolean isEqualityType() {
        return t1.isEqualityType() && t2.isEqualityType();
    }

    @Override public Substitution unify(Type other) throws TypeError {
        if (other instanceof TypeVar) {
            return other.unify(this);
        }
        else if (other instanceof PairType) {
            return t1.unify(((PairType) other).t1).compose(t2.unify(((PairType) other).t2));
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
        return new PairType(t1.clone(), t2.clone());
    }

    @Override public boolean contains(TypeVar tv) {
        return t1.contains(tv) || t2.contains(tv);
    }

    @Override public Type replace(TypeVar a, Type t) {
        return new PairType(t1.replace(a, t), t2.replace(a, t));
    }

    public String toString() {
        return "(" + t1 + " * " + t2 + ")";
    }
}
