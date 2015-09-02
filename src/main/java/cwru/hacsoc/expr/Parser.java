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

public class Parser {

    public static class Error extends Exception {
        public Match match;
        public Error(String msg, Match m) {
            super(String.format("Parse error at %s : %s", m, msg));
            this.match = m;
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

    public static Node Parse(Lexer l) throws Lexer.Error, Error {
        Parser p = new Parser(l);
        Node t = p.Expr();
        if (!l.EOF()) {
            throw new UnconsumedInput(l.peek());
        }
        return t;
    }

    Node collapse(Node subtree, Node extra) {
        if (extra == null) {
            return subtree;
        }
        Node op = (Node)extra.kids.get(0);
        op.kids.add(0, subtree);
        return (Node)extra.kids.get(1);
    }

    Node swing(Node a, Node b, Node c) {
        return (new Node("T"))
                    .addkid(a)
                    .addkid(collapse(a.addkid(b), c));
    }

    Node Expr() throws Lexer.Error, Error {
        return collapse(Term(), Expr_());
    }

    Node Expr_() throws Lexer.Error, Error {
        Match m = lex.peek();
        switch (m.token) {
            case PLUS: case DASH:
                return swing(new Node(lex.next().lexeme), Term(), Expr_());
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
                return swing(new Node(lex.next().lexeme), Unary(), Term_());
            default: // epsilon
                return null;
        }
    }

    Node Unary() throws Lexer.Error, Error {
        Match m = lex.peek();
        switch (m.token) {
            case DASH:
                return (new Node(lex.next().lexeme)).addkid(Factor());
            default:
                return Factor();
        }
    }

    Node Factor() throws Lexer.Error, Error {
        Match m = lex.peek();
        switch (m.token) {
            case NUMBER:
                return new Node(lex.next().lexeme);
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
}

