package simpl.typing;

final class IntType extends Type {

    protected IntType() {
    }

    @Override public boolean isEqualityType() {
        return true;
    }

    @Override public Substitution unify(Type other) throws TypeError {
        if (other instanceof TypeVar) {
            return other.unify(this);
        }
        else if (other instanceof IntType) {
            return Substitution.IDENTITY;
        }
        throw new TypeMismatchError();
    }

    @Override public boolean contains(TypeVar tv) {
        return false;
    }

    @Override public Type replace(TypeVar a, Type t) {
        return Type.INT;
    }

    public String toString() {
        return "int";
    }
}
