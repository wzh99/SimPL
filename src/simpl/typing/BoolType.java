package simpl.typing;

final class BoolType extends Type {

    protected BoolType() {
    }

    @Override public boolean isEqualityType() {
        return true;
    }

    @Override public Substitution unify(Type other) throws TypeError {
        if (other instanceof TypeVar) {
            return other.unify(this);
        }
        else if (other instanceof BoolType) {
            return Substitution.IDENTITY;
        }
        throw new TypeMismatchError(this, other);
    }

    @Override public boolean contains(TypeVar tv) {
        return false;
    }

    @Override public Type replace(TypeVar a, Type t) {
        return Type.BOOL;
    }

    public String toString() {
        return "bool";
    }
}
