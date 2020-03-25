package simpl.parser.ast;

import simpl.interpreter.RefValue;
import simpl.interpreter.RuntimeError;
import simpl.interpreter.State;
import simpl.interpreter.Value;
import simpl.typing.*;

public class Assign extends BinaryExpr {

    public Assign(Expr l, Expr r) {
        super(l, r);
    }

    public String toString() {
        return l + " := " + r;
    }

    @Override public TypeResult typeCheck(TypeEnv E) throws TypeError {
        // Check type for reference cell
        var refTy = l.typeCheck(E);
        var subst = refTy.s;
        Type cellTy;
        if (refTy.t instanceof RefType) {
            cellTy = ((RefType) refTy.t).t;
        }
        else if (refTy.t instanceof TypeVar) {
            // Infer type for type variable
            var cellTv = new TypeVar(true);
            subst = refTy.t.unify(new RefType(cellTv)).compose(subst);
            cellTy = subst.apply(cellTv);
        }
        else {
            throw new TypeError("not reference type");
        }
        // Check assigned value
        var assignTy = r.typeCheck(E);
        subst = assignTy.t.unify(cellTy).compose(assignTy.s).compose(subst);
        return TypeResult.of(subst, Type.UNIT);
    }

    @Override public Value eval(State s) throws RuntimeError {
        var refVal = l.eval(s);
        if (!(refVal instanceof RefValue)) {
            throw new RuntimeError("not a reference");
        }
        var assignVal = r.eval(s);
        s.M.write(((RefValue) refVal).p, assignVal);
        return Value.UNIT;
    }
}
