package simpl.parser.ast;

import simpl.typing.Type;
import simpl.typing.TypeEnv;
import simpl.typing.TypeError;
import simpl.typing.TypeResult;

public abstract class ArithExpr extends BinaryExpr {

    public ArithExpr(Expr l, Expr r) {
        super(l, r);
    }

    @Override public TypeResult typeCheck(TypeEnv E) throws TypeError {
        // Check types of both operands
        var lhsTy = l.typeCheck(E);
        var rhsTy = r.typeCheck(E);
        var subst = lhsTy.s.compose(rhsTy.s);

        // Unify both types to `int`
        subst = subst.compose(lhsTy.t.unify(Type.INT));
        subst = subst.compose(rhsTy.t.unify(Type.INT));

        // Return typing result
        return TypeResult.of(subst, Type.INT);
    }
}
