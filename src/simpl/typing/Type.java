package simpl.typing;

import java.util.Set;

public abstract class Type {

    public abstract boolean isEqualityType();

    public abstract Type replace(TypeVar a, Type t);

    public abstract boolean contains(TypeVar tv);

    public abstract Substitution unify(Type other) throws TypeError;

    public abstract Set<TypeVar> collect();

    public abstract Type clone();

    public static final Type INT = new IntType();
    public static final Type BOOL = new BoolType();
    public static final Type UNIT = new UnitType();
}
