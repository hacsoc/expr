package cwru.hacsoc.expr;

import static org.junit.Assert.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import org.junit.Test;
import org.junit.Ignore;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.InputStream;
import java.io.ByteArrayInputStream;

public class test_Lexer {

    InputStream stream(String str) {
        return new ByteArrayInputStream(str.getBytes());
    }

    @Test
    public void test_start_plus() throws Lexer.Error {
        Lexer l = new Lexer(null);
        Match m = l.start('+', false);
        assertThat(m, is(new Match(Tokens.PLUS, "+", 1, 0)));
    }

    @Test
    public void test_start_dash() throws Lexer.Error {
        Lexer l = new Lexer(null);
        Match m = l.start('-', false);
        assertThat(m, is(new Match(Tokens.DASH, "-", 1, 0)));
    }

    @Test
    public void test_start_star() throws Lexer.Error {
        Lexer l = new Lexer(null);
        Match m = l.start('*', false);
        assertThat(m, is(new Match(Tokens.STAR, "*", 1, 0)));
    }

    @Test
    public void test_start_slash() throws Lexer.Error {
        Lexer l = new Lexer(null);
        Match m = l.start('/', false);
        assertThat(m, is(new Match(Tokens.SLASH, "/", 1, 0)));
    }

    @Test
    public void test_start_lparen() throws Lexer.Error {
        Lexer l = new Lexer(null);
        Match m = l.start('(', false);
        assertThat(m, is(new Match(Tokens.LPAREN, "(", 1, 0)));
    }

    @Test
    public void test_start_rparen() throws Lexer.Error {
        Lexer l = new Lexer(null);
        Match m = l.start(')', false);
        assertThat(m, is(new Match(Tokens.RPAREN, ")", 1, 0)));
    }

    @Test
    public void test_start_white() throws Lexer.Error {
        Lexer l = new Lexer(null);
        Match m = l.start(' ', false);
        assertThat(m, nullValue());
        m = l.start('\t', false);
        assertThat(m, nullValue());
        m = l.start('\n', false);
        assertThat(m, nullValue());
    }

    @Test
    public void test_start_eof() throws Lexer.Error {
        Lexer l = new Lexer(null);
        Match m = l.start((char)0, true);
        assertThat(m, is(new Match(Tokens.EOF, "", 1, 0)));
    }

    @Test
    public void test_start_num() throws Lexer.Error {
        for (int i = 0; i < 10; i++) {
            Lexer l = new Lexer(null);
            String s = String.format("%d", i);
            char b = (char) s.getBytes()[0];
            Match m = l.start(b, false);
            assertThat(m, nullValue());
            assertThat(l.buf.toString(), is(s));
        }
    }

    @Test(expected=Lexer.UnexpectedCharacter.class)
    public void test_start_bad() throws Lexer.Error {
        Lexer l = new Lexer(null);
        Match m = l.start('a', false);
    }

    @Test
    public void test_number_num() throws Lexer.Error {
        for (int i = 0; i < 10; i++) {
            Lexer l = new Lexer(null);
            String s = String.format("%d", i);
            char b = (char) s.getBytes()[0];
            Match m = l.number(b, false);
            assertThat(m, nullValue());
            assertThat(l.buf.toString(), is(s));
        }
    }

    @Test
    public void test_number_plus() throws Lexer.Error {
        Lexer l = new Lexer(null);
        Match m = l.start('3', false);
        assertThat(m, nullValue());
        assertThat(l.buf.toString(), is("3"));
        m = l.number('+', false);
        assertThat(m, is(new Match(Tokens.NUMBER, "3", 1, 0)));
        assertThat(l.peeked.peekFirst(), is(new Match(Tokens.PLUS, "+", 1, 0)));
    }

    @Test
    public void test_transition_number_plus() throws Lexer.Error {
        Lexer l = new Lexer(null);
        assertThat(l.state, is(Lexer.States.Start));
        Match m = l.transition('3', false);
        assertThat(m, nullValue());
        assertThat(l.buf.toString(), is("3"));
        assertThat(l.state, is(Lexer.States.Number));
        m = l.transition('+', false);
        assertThat(m, is(new Match(Tokens.NUMBER, "3", 1, 0)));
        assertThat(l.peeked.peekFirst(), is(new Match(Tokens.PLUS, "+", 1, 0)));
        assertThat(l.state, is(Lexer.States.Start));
    }

    @Test(expected=Lexer.Error.class)
    public void test_transition_null_state() throws Lexer.Error {
        Lexer l = new Lexer(null);
        l.state = null;
        l.transition('3', false);
    }

    @Test(expected=Lexer.Error.class)
    public void test_transition_eof_state() throws Lexer.Error {
        Lexer l = new Lexer(null);
        l.state = Lexer.States.EOF;
        l.transition('3', false);
    }

    @Test(expected=Lexer.EOF.class)
    public void test_nextChar_2chars() throws Lexer.Error {
        Lexer l = new Lexer(stream("ab"));
        assertThat(l.nextChar(), is('a'));
        assertThat(l.nextChar(), is('b'));
        assertThat(l.line, is(1));
        assertThat(l.col, is(2));
        l.nextChar();
    }

    @Test(expected=Lexer.EOF.class)
    public void test_nextChar_3chars_newline() throws Lexer.Error {
        Lexer l = new Lexer(stream("a\nb"));
        assertThat(l.nextChar(), is('a'));
        assertThat(l.line, is(1));
        assertThat(l.col, is(1));
        assertThat(l.nextChar(), is('\n'));
        assertThat(l.line, is(2));
        assertThat(l.col, is(0));
        assertThat(l.nextChar(), is('b'));
        assertThat(l.line, is(2));
        assertThat(l.col, is(1));
        l.nextChar();
    }

    @Test
    public void test_next_1_plus_2() throws Lexer.Error {
        Lexer l = new Lexer(stream("1 +2"));
        assertThat(l.next(), is(new Match(Tokens.NUMBER, "1", 1, 1)));
        assertThat(l.next(), is(new Match(Tokens.PLUS, "+", 1, 3)));
        assertThat(l.next(), is(new Match(Tokens.NUMBER, "2", 1, 4)));
        assertThat(l.next(), is(new Match(Tokens.EOF, "", 1, 5)));
    }

    @Test
    public void test_peek_1_plus_2() throws Lexer.Error {
        Lexer l = new Lexer(stream("1 +2"));
        assertThat(l.peek(), is(new Match(Tokens.NUMBER, "1", 1, 1)));
        assertThat(l.peek(), is(new Match(Tokens.NUMBER, "1", 1, 1)));
        assertThat(l.peek(), is(new Match(Tokens.NUMBER, "1", 1, 1)));
        assertThat(l.next(), is(new Match(Tokens.NUMBER, "1", 1, 1)));
        assertThat(l.peek(), is(new Match(Tokens.PLUS, "+", 1, 3)));
        assertThat(l.peek(), is(new Match(Tokens.PLUS, "+", 1, 3)));
        assertThat(l.peek(), is(new Match(Tokens.PLUS, "+", 1, 3)));
        assertThat(l.next(), is(new Match(Tokens.PLUS, "+", 1, 3)));
        assertThat(l.peek(), is(new Match(Tokens.NUMBER, "2", 1, 4)));
        assertThat(l.peek(), is(new Match(Tokens.NUMBER, "2", 1, 4)));
        assertThat(l.peek(), is(new Match(Tokens.NUMBER, "2", 1, 4)));
        assertThat(l.peek(), is(new Match(Tokens.NUMBER, "2", 1, 4)));
        assertThat(l.peek(), is(new Match(Tokens.NUMBER, "2", 1, 4)));
        assertThat(l.peek(), is(new Match(Tokens.NUMBER, "2", 1, 4)));
        assertThat(l.next(), is(new Match(Tokens.NUMBER, "2", 1, 4)));
        assertThat(l.peek(), is(new Match(Tokens.EOF, "", 1, 5)));
        assertThat(l.peek(), is(new Match(Tokens.EOF, "", 1, 5)));
        assertThat(l.peek(), is(new Match(Tokens.EOF, "", 1, 5)));
        assertThat(l.peek(), is(new Match(Tokens.EOF, "", 1, 5)));
        assertThat(l.next(), is(new Match(Tokens.EOF, "", 1, 5)));
    }

    @Test
    public void test_lex_expr() throws Lexer.Error {
        Lexer l = new Lexer(stream("1 + 2*(3-4)"));
        assertThat(l.next(), is(new Match(Tokens.NUMBER, "1", 1, 1)));
        assertThat(l.next(), is(new Match(Tokens.PLUS, "+", 1, 3)));
        assertThat(l.next(), is(new Match(Tokens.NUMBER, "2", 1, 5)));
        assertThat(l.next(), is(new Match(Tokens.STAR, "*", 1, 6)));
        assertThat(l.next(), is(new Match(Tokens.LPAREN, "(", 1, 7)));
        assertThat(l.next(), is(new Match(Tokens.NUMBER, "3", 1, 8)));
        assertThat(l.next(), is(new Match(Tokens.DASH, "-", 1, 9)));
        assertThat(l.next(), is(new Match(Tokens.NUMBER, "4", 1, 10)));
        assertThat(l.next(), is(new Match(Tokens.RPAREN, ")", 1, 11)));
        assertThat(l.next(), is(new Match(Tokens.EOF, "", 1, 12)));
    }
}

