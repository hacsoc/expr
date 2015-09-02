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

public class test_Parser {

    InputStream stream(String str) {
        return new ByteArrayInputStream(str.getBytes());
    }

    Lexer lexer(String str) {
        return new Lexer(stream(str));
    }

    @Test
    public void test_simple_expr() throws Lexer.Error, Parser.Error {
        Node t = Parser.Parse(lexer("-1 + 2*(3-1)"));
        assertThat(t.toString(), is("(+ (- 1) (* 2 (- 3 1)))"));
    }

    @Test(expected=Parser.UnexpectedToken.class)
    public void test_unexpected_eof() throws Lexer.Error, Parser.Error {
        System.out.println(Parser.Parse(lexer("1 + 2 -")));
    }

    @Test(expected=Parser.UnexpectedToken.class)
    public void test_unclosed() throws Lexer.Error, Parser.Error {
        System.out.println(Parser.Parse(lexer("1 + (2 - 3")));
    }

    @Test(expected=Parser.UnconsumedInput.class)
    public void test_runon() throws Lexer.Error, Parser.Error {
        System.out.println(Parser.Parse(lexer("1 + 2 - 3 4")));
    }
}
