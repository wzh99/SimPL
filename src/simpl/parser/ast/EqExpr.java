package simpl.parser.ast;

import simpl.typing.Type;
import simpl.typing.TypeEnv;
import simpl.typing.TypeError;
import simpl.typing.TypeResult;

public abstract class EqExpr extends BinaryExpr {

    public EqExpr(Expr l, Expr r) {
        super(l, r);
    }

    @Override public TypeResult typeCheck(TypeEnv E) throws TypeError {
        // Check types of both operands
        var lhsTr = l.typeCheck(E);
        var rhsTr = r.typeCheck(E);
        var subst = lhsTr.s.compose(rhsTr.s);
        var lhsTy = subst.apply(lhsTr.t);
        var rhsTy = subst.apply(rhsTr.t);

        // Unify types of both operands
        subst = subst.compose(lhsTy.unify(rhsTy));
        lhsTy = subst.apply(lhsTy);
        rhsTy = subst.apply(rhsTy);
        if (!lhsTy.isEqualityType()) {
            throw new TypeError("lhs is not equality type");
        }
        if (!rhsTy.isEqualityType()) {
            throw new TypeError("rhs is not equality type");
        }
        // Return typing result
        return TypeResult.of(subst, Type.BOOL);
    }
}
