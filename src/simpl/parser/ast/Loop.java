package simpl.parser.ast;

import simpl.interpreter.BoolValue;
import simpl.interpreter.RuntimeError;
import simpl.interpreter.State;
import simpl.interpreter.Value;
import simpl.typing.Type;
import simpl.typing.TypeEnv;
import simpl.typing.TypeError;
import simpl.typing.TypeResult;

public class Loop extends Expr {

    public Expr e1, e2;

    public Loop(Expr e1, Expr e2) {
        this.e1 = e1;
        this.e2 = e2;
    }

    public String toString() {
        return "(while " + e1 + " do " + e2 + ")";
    }

    @Override public TypeResult typeCheck(TypeEnv E) throws TypeError {
        // Check predicate type
        var predTy = e1.typeCheck(E);
        var subst = predTy.s.compose(predTy.t.unify(Type.BOOL));
        // Check loop body type
        var bodyTy = e2.typeCheck(E);
        subst = subst.compose(bodyTy.t.unify(Type.UNIT));
        // Return unit type
        return TypeResult.of(subst, Type.UNIT);
    }

    @Override public Value eval(State s) throws RuntimeError {
        var condVal = e1.eval(s);
        if (!(condVal instanceof BoolValue)) {
            throw new RuntimeError("not a boolean");
        }
        if (((BoolValue) condVal).b) {
            return new Seq(e2, this).eval(s);
        }
        else {
            return Value.UNIT;
        }
    }
}
