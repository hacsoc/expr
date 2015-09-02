package cwru.hacsoc.expr;

/* Grammar
 *
 * E -> T E' ;
 * E' -> + T E'
 *     | - T E'
 *     | e
 *     ;
 * T -> U T' ;
 * T' -> * U T'
 *     | / U T'
 *     | e
 *     ;
 * U -> - F
 *    | F
 *    ;
 * F -> ( E )
 *    | NUMBER
 *    ;
 */

import java.lang.StringBuilder;
import java.lang.NumberFormatException;

public class Parser {

    public static class Error extends Exception {
        public Match match;
        public Error(String msg, Match m) {
            super(format(msg, m));
            this.match = m;
        }
        static String format(String msg, Match m) {
            if (m != null) {
                return String.format("Parse error at %s : %s", m, msg);
            } else {
                return String.format("Parse error : %s", msg);
            }
        }
    }

    public static class LexerError extends Error {
        Lexer.Error e;
        public LexerError(Lexer.Error e) {
            super(e.toString(), null);
            this.e = e;
        }
    }

    public static class UnconsumedInput extends Error {
        public UnconsumedInput(Match m) {
            super(String.format("Unconsumed input"), m);
        }
    }

    public static class UnexpectedToken extends Error {
        public UnexpectedToken(Match m, Tokens... expected) {
            super(String.format("Unexpected token %s : expected %s", m.token, join(expected)), m);
        }
        public static String join(Tokens[] expected) {
            StringBuilder sb = new StringBuilder();
            for (Tokens t : expected) {
                sb.append(t);
                sb.append(" ");
            }
            return sb.toString();
        }
    }

    Lexer lex;

    private Parser(Lexer lex) {
        this.lex = lex;
    }

    public static Node Parse(Lexer l) throws Error {
        Parser p = new Parser(l);
        Node t;
        try {
            t = p.Expr();
            if (!l.EOF()) {
                throw new UnconsumedInput(l.peek());
            }
        } catch (Lexer.Error e) {
            throw new LexerError(e);
        }
        return t;
    }

    Node collapse(Node subtree, Node extra) {
        if (extra == null) {
            return subtree;
        }
        // extra == T
        //         / \
        //       op   root
        //             .
        //             .
        //             .
        //          op
        // returns
        //           root
        //             .
        //             .
        //             .
        //          op
        //            \
        //             subtree
        Node op = (Node)extra.kids.get(0);
        op.kids.add(0, subtree);
        return (Node)extra.kids.get(1);
    }

    Node swing(Node op, Node left, Node extra) {
        return (new Node("T"))
                    .addkid(op)
                    .addkid(collapse(op.addkid(left), extra));
    }

    Node Expr() throws Lexer.Error, Error {
        return collapse(Term(), Expr_());
    }

    Node Expr_() throws Lexer.Error, Error {
        Match m = lex.peek();
        switch (m.token) {
            case PLUS: case DASH:
                return swing(new Node<String>(lex.next().lexeme), Term(), Expr_());
            default: // epsilon
                return null;
        }
    }

    Node Term() throws Lexer.Error, Error {
        return collapse(Unary(), Term_());
    }

    Node Term_() throws Lexer.Error, Error {
        Match m = lex.peek();
        switch (m.token) {
            case STAR: case SLASH:
                return swing(new Node<String>(lex.next().lexeme), Unary(), Term_());
            default: // epsilon
                return null;
        }
    }

    Node Unary() throws Lexer.Error, Error {
        Match m = lex.peek();
        switch (m.token) {
            case DASH:
                return (new Node<String>(lex.next().lexeme)).addkid(Factor());
            default:
                return Factor();
        }
    }

    Node Factor() throws Lexer.Error, Error {
        Match m = lex.peek();
        switch (m.token) {
            case NUMBER:
                return new Node<Integer>(atoi(lex.next()));
            case LPAREN:
                lex.next();
                Node e = Expr();
                Match r = lex.next();
                if (r.token != Tokens.RPAREN) {
                    throw new UnexpectedToken(r, Tokens.RPAREN);
                }
                return e;
            default:
                throw new UnexpectedToken(m, Tokens.NUMBER, Tokens.LPAREN);
        }
    }

    Integer atoi(Match m) throws Error {
        try {
            return Integer.parseInt(m.lexeme);
        } catch (NumberFormatException e) {
            throw new Error(String.format("Could not parse number from match : %s", e), m);
        }
    }
}

