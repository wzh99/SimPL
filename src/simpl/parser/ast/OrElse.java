package simpl.parser.ast;

import simpl.interpreter.BoolValue;
import simpl.interpreter.RuntimeError;
import simpl.interpreter.State;
import simpl.interpreter.Value;
import simpl.typing.Type;
import simpl.typing.TypeEnv;
import simpl.typing.TypeError;
import simpl.typing.TypeResult;

public class OrElse extends BinaryExpr {

    public OrElse(Expr l, Expr r) {
        super(l, r);
    }

    public String toString() {
        return "(" + l + " orelse " + r + ")";
    }

    @Override public TypeResult typeCheck(TypeEnv E) throws TypeError {
        var lhsTr = l.typeCheck(E);
        var rhsTr = l.typeCheck(E);
        var subst = lhsTr.s.compose(rhsTr.s);
        var lhsTy = subst.apply(lhsTr.t);
        var rhsTy = subst.apply(rhsTr.t);
        subst = subst.compose(lhsTy.unify(Type.BOOL));
        subst = subst.compose(rhsTy.unify(Type.BOOL));
        return TypeResult.of(subst, Type.BOOL);
    }

    @Override public Value eval(State s) throws RuntimeError {
        var lhsVal = l.eval(s);
        if (!(lhsVal instanceof BoolValue)) {
            throw new RuntimeError("lhs is not boolean");
        }
        if (((BoolValue) lhsVal).b) { // short circuit if lhs evaluates to true
            return new BoolValue(true);
        }
        var rhsVal = r.eval(s);
        if (!(rhsVal instanceof BoolValue)) {
            throw new RuntimeError("rhs is not boolean");
        }
        return rhsVal;
    }
}
