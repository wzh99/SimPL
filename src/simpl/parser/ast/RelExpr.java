package simpl.parser.ast;

import simpl.typing.Type;
import simpl.typing.TypeEnv;
import simpl.typing.TypeError;
import simpl.typing.TypeResult;

public abstract class RelExpr extends BinaryExpr {

    public RelExpr(Expr l, Expr r) {
        super(l, r);
    }

    @Override public TypeResult typeCheck(TypeEnv E) throws TypeError {
        // Check types of both operands
        var t1 = l.typeCheck(E);
        var t2 = r.typeCheck(E);

        // Unify both types to `int`
        var subst = t1.s.compose(t2.s);
        subst = subst.compose(t1.t.unify(Type.INT)).compose(t2.t.unify(Type.INT));

        // Return typing result
        return TypeResult.of(subst, Type.BOOL);
    }
}
