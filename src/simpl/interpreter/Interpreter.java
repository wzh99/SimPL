package simpl.interpreter;

import simpl.parser.Parser;
import simpl.parser.ast.Expr;
import simpl.typing.DefaultTypeEnv;
import simpl.typing.TypeError;

import java.io.FileInputStream;
import java.io.InputStream;

public class Interpreter {

    public void run(String filename) {
        try (InputStream inp = new FileInputStream(filename)) {
            Parser parser = new Parser(inp);
            java_cup.runtime.Symbol parseTree = parser.parse();
            Expr program = (Expr) parseTree.value;
            program.typeCheck(new DefaultTypeEnv());
            //            var tr = program.typeCheck(new DefaultTypeEnv());
            //            System.out.println(tr.t);
            System.out.println(program.eval(new InitialState()));
        }
        catch (TypeError e) {
            //            e.printStackTrace();
            System.out.println("type error");
        }
        catch (RuntimeError e) {
            //            e.printStackTrace();
            System.out.println("runtime error");
        }
        catch (Exception e) {
            System.out.println("syntax error");
        }
    }

    private static void interpret(String filename) {
        Interpreter i = new Interpreter();
        //        System.out.println(filename);
        i.run(filename);
    }

    public static void main(String[] args) {
        interpret(args[0]);
        //        // Provided programs
        //        interpret("doc/examples/plus.spl");
        //        interpret("doc/examples/factorial.spl");
        //        interpret("doc/examples/gcd1.spl");
        //        interpret("doc/examples/gcd2.spl");
        //        interpret("doc/examples/max.spl");
        //        interpret("doc/examples/sum.spl");
        //        interpret("doc/examples/map.spl");
        //        interpret("doc/examples/pcf.sum.spl");
        //        interpret("doc/examples/pcf.even.spl");
        //        interpret("doc/examples/pcf.minus.spl");
        //        interpret("doc/examples/pcf.factorial.spl");
        //        interpret("doc/examples/pcf.fibonacci.spl");
        //        interpret("doc/examples/letpoly.spl");
        //
        //        // Programs written by myself
        //        interpret("doc/examples/gc.spl");
        //        interpret("doc/examples/mrc.even.spl");
        //        interpret("doc/examples/stream.spl");
        //        interpret("doc/examples/circ.spl");
        //        interpret("doc/examples/foreach.spl");
        //        interpret("doc/examples/reduce.spl");
        //        interpret("doc/examples/filter.spl");
    }
}
