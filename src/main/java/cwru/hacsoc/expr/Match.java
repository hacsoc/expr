package cwru.hacsoc.expr;

public class Match {
    public int line;
    public int column;
    public Tokens token;
    public String lexeme;

    public Match(Tokens token, String lexeme, int line, int column) {
        this.token = token;
        this.lexeme = lexeme;
        this.line = line;
        this.column = column;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Match) {
            return this.equals((Match)o);
        }
        return false;
    }

    public boolean equals(Match m) {
        if (lexeme != null && !lexeme.equals(m.lexeme)) {
            return false;
        }
        return line == m.line && column == m.column && token == m.token;
    }

    @Override
    public int hashCode() {
        int max = Integer.MAX_VALUE;
        int hash = (line*3 + column*5) % max;
        if (lexeme != null) {
            hash = (hash * 7 * (lexeme.hashCode() + 1)) % max;
        } else {
            hash = (hash * 7) % max;
        }
        if (token != null) {
            hash = (hash * 11 * (token.hashCode() + 1)) % max;
        } else {
            hash = (hash * 11) % max;
        }
        return hash;
    }

    @Override
    public String toString() {
        return String.format("<Match %s '%s' %d:%d>", token, lexeme, line, column);
    }
}
