package simpl.typing;

public final class ListType extends Type {

    public Type t;

    public ListType(Type t) {
        this.t = t;
    }

    @Override public boolean isEqualityType() {
        return true;
    }

    @Override public Substitution unify(Type other) throws TypeError {
        if (other instanceof TypeVar) {
            return other.unify(this);
        }
        else if (other instanceof ListType) {
            t.unify(((ListType) other).t);
        }
        throw new TypeMismatchError();
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
