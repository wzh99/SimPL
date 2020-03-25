package simpl.parser.ast;

import simpl.interpreter.RefValue;
import simpl.interpreter.RuntimeError;
import simpl.interpreter.State;
import simpl.interpreter.Value;
import simpl.typing.RefType;
import simpl.typing.TypeEnv;
import simpl.typing.TypeError;
import simpl.typing.TypeResult;

public class Ref extends UnaryExpr {

    public Ref(Expr e) {
        super(e);
    }

    public String toString() {
        return "(ref " + e + ")";
    }

    @Override public TypeResult typeCheck(TypeEnv E) throws TypeError {
        var valTy = e.typeCheck(E);
        return TypeResult.of(valTy.s, new RefType(valTy.t));
    }

    @Override public Value eval(State s) throws RuntimeError {
        // Evaluate the expression to be stored
        var cellVal = e.eval(s);
        // Get pointer for this cell
        var ptr = s.M.alloc(s);
        s.M.write(ptr, cellVal);
        // Print address of allocated cell to test GC
        // System.out.println("ref@" + curPtr);
        // Return reference
        return new RefValue(ptr);
    }
}
