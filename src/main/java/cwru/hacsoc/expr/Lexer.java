package cwru.hacsoc.expr;

/* Tokens
 *
 * NUMBER := [0-9]+
 * PLUS := \+
 * DASH := -
 * STAR := \*
 * SLASH := /
 */

import java.io.InputStream;
import java.lang.StringBuilder;
import java.util.LinkedList;

public class Lexer {

    public static class Error extends Exception {
        public int startLine;
        public int startCol;
        public Error(String msg, int startLine, int startCol) {
            super(String.format("Lexer error at line %d col %d %s", startLine, startCol, msg));
            this.startLine = startLine;
            this.startCol = startCol;
        }
        public Error(String msg, int startLine, int startCol, Exception cause) {
            super(String.format("Lexer error at line %d col %d %s", startLine, startCol, msg), cause);
            this.startLine = startLine;
            this.startCol = startCol;
        }
    }

    public static class UnexpectedCharacter extends Error {
        public UnexpectedCharacter(String msg, int startLine, int startCol) {
            super(msg, startLine, startCol);
        }
    }

    public static class IOException extends Error {
        java.io.IOException orig;
        public IOException(java.io.IOException e, int startLine, int startCol) {
            super(e.toString(), startLine, startCol, e);
        }
    }

    static class EOF extends Error {
        public EOF(int startLine, int startCol) {
            super("End of file", startLine, startCol);
        }
    }


    static enum States {
        Start, Number, EOF;
    }

    InputStream in;
    int line = 1;
    int col = 0;
    int s_line = 1;
    int s_col = 0;
    States state = States.Start;
    StringBuilder buf = new StringBuilder();
    LinkedList<Match> peeked = new LinkedList<Match>();

    public Lexer(InputStream in) {
        this.in = in;
    }

    public boolean EOF() {
        if (state == States.EOF && peeked.isEmpty()) {
            return true;
        }
        if (!peeked.isEmpty()) {
            return peeked.peekFirst().token == Tokens.EOF;
        }
        return false;
    }

    public Match peek() throws Error {
        if (peeked.isEmpty()) {
            // since peeked is empty the match will
            // always be first. There may have been future
            // matches already added to peeked so .add is not
            // safe
            peeked.addFirst(next());
        }
        return peeked.peekFirst();
    }

    public Match next() throws Error {
        if (!peeked.isEmpty()) {
            return peeked.removeFirst();
        }
        while (true) {
            Match m;
            try {
                m = transition(nextChar(), false);
            } catch (EOF e) {
                m = transition((char)0, true);
            }
            if (m != null) {
                return m;
            }
        }
    }

    char nextChar() throws Error {
        if (state == States.EOF) {
            throw new EOF(line, col);
        }
        int c;
        try {
            c = in.read();
        } catch (java.io.IOException e) {
            throw new IOException(e, line, col);
        }
        if (c < 0) {
            col++;
            throw new EOF(line, col);
        }
        char chr = (char)c;
        switch (chr) {
            case '\n':
                line++;
                col = 0;
                break;
            default:
                col++;
        }
        return chr;
    }

    Match transition(char chr, boolean eof) throws Error {
        if (state == null) {
            throw new Error("Null State", line, col);
        }
        switch (state) {
            case Start:
                return start(chr, eof);
            case Number:
                return number(chr, eof);
            default:
                throw new Error(String.format("Unexpected State %s", state), line, col);
        }
    }

    Match start(char chr, boolean eof) throws Error {
        s_line = line;
        s_col = col;
        if (eof) {
            state = States.EOF;
            return new Match(Tokens.EOF, "", line, col);
        }
        switch (chr) {
            case '+':
                return new Match(Tokens.PLUS, "+", s_line, s_col);
            case '-':
                return new Match(Tokens.DASH, "-", s_line, s_col);
            case '*':
                return new Match(Tokens.STAR, "*", s_line, s_col);
            case '/':
                return new Match(Tokens.SLASH, "/", s_line, s_col);
            case '(':
                return new Match(Tokens.LPAREN, "(", s_line, s_col);
            case ')':
                return new Match(Tokens.RPAREN, ")", s_line, s_col);
            case '0': case '1': case '2': case '3': case '4': case '5':
            case '6': case '7': case '8': case '9':
                buf.append(chr);
                state = States.Number;
                return null;
            case ' ': case '\n': case '\t':
                // skip whitespace
                return null;
            default:
                throw new UnexpectedCharacter(String.format("unexpected char %c", chr), line, col);
        }
    }

    Match number(char chr, boolean eof) throws Error {
        switch (chr) {
            case '0': case '1': case '2': case '3': case '4': case '5':
            case '6': case '7': case '8': case '9':
                buf.append(chr);
                return null;
            default:
                Match m = new Match(Tokens.NUMBER, buf.toString(), s_line, s_col);
                buf = new StringBuilder();
                state = States.Start;
                Match s = start(chr, eof);
                if (s != null) {
                    peeked.add(s);
                }
                return m;
        }
    }
}

