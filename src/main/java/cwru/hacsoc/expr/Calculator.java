package cwru.hacsoc.expr;

import java.io.ByteArrayInputStream;
import java.io.InputStream;


public class Calculator {

    static InputStream stream(String str) {
        return new ByteArrayInputStream(str.getBytes());
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Expected exactly one expression");
            System.err.println("Ex: java Calculator '1 + 2 * 3'");
            System.exit(1);
        }

        Node t = null;
        try {
            t = Parser.Parse(new Lexer(stream(args[0])));
        } catch (Parser.Error e) {
            System.err.println(e);
            System.exit(1);
        }
        System.out.println(t);
        try {
            System.out.println(Eval.evaluate(t));
        } catch (Eval.Error e) {
            System.err.println(e);
            System.exit(1);
        }
    }

}

