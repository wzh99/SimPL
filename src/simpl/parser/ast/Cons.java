package simpl.parser.ast;

import simpl.interpreter.ConsValue;
import simpl.interpreter.RuntimeError;
import simpl.interpreter.State;
import simpl.interpreter.Value;
import simpl.typing.ListType;
import simpl.typing.TypeEnv;
import simpl.typing.TypeError;
import simpl.typing.TypeResult;

public class Cons extends BinaryExpr {

    public Cons(Expr l, Expr r) {
        super(l, r);
    }

    public String toString() {
        return "(" + l + " :: " + r + ")";
    }

    @Override public TypeResult typeCheck(TypeEnv E) throws TypeError {
        // Check type of concatenated element
        var elemTr = l.typeCheck(E);
        var elemTy = elemTr.t;
        // Unify list type with type of list of element
        var listTr = r.typeCheck(E);
        var subst = listTr.s.compose(elemTr.s);
        var listTy = subst.apply(listTr.t);
        elemTy = subst.apply(elemTy);
        subst = listTy.unify(new ListType(elemTy)).compose(subst);
        // Return list type
        listTy = subst.apply(listTy);
        return TypeResult.of(subst, listTy);
    }

    @Override public Value eval(State s) throws RuntimeError {
        var elemVal = l.eval(s);
        var listVal = r.eval(s);
        return new ConsValue(elemVal, listVal);
    }
}
