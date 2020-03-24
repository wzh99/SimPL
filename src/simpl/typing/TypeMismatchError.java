package simpl.typing;

public class TypeMismatchError extends TypeError {

    private static final long serialVersionUID = -9010997809568642250L;

    public TypeMismatchError(Type t1, Type t2) {
        super(String.format("type %s does not match %s", t1, t2));
    }
}
