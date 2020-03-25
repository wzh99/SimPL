package simpl.parser.ast;

import simpl.interpreter.RefValue;
import simpl.interpreter.RuntimeError;
import simpl.interpreter.State;
import simpl.interpreter.Value;
import simpl.typing.*;

public class Deref extends UnaryExpr {

    public Deref(Expr e) {
        super(e);
    }

    public String toString() {
        return "!" + e;
    }

    @Override public TypeResult typeCheck(TypeEnv E) throws TypeError {
        // Check type of reference
        var refTy = e.typeCheck(E);
        // Return type result
        if (refTy.t instanceof RefType) { // known to be reference type
            return TypeResult.of(refTy.s, ((RefType) refTy.t).t);
        }
        else if (refTy.t instanceof TypeVar) {
            // Unify type variable with reference type
            var cellTv = new TypeVar(true);
            var subst = refTy.s.compose(refTy.t.unify(new RefType(cellTv)));
            return TypeResult.of(subst, cellTv);
        }
        throw new TypeError("not reference type");
    }

    @Override public Value eval(State s) throws RuntimeError {
        // Evaluate reference value
        var refVal = e.eval(s);
        if (!(refVal instanceof RefValue)) {
            throw new RuntimeError("not a reference");
        }
        // Read memory to get content of the reference cell
        return s.M.read(((RefValue) refVal).p);
    }
}
