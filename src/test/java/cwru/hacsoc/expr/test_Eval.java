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

public class test_Eval {

    Node parse(String str) throws Parser.Error {
        Lexer l = new Lexer(new ByteArrayInputStream(str.getBytes()));
        return Parser.Parse(l);
    }

    @Test
    public void test_echo() throws Parser.Error, Eval.Error {
        assertThat(Eval.evaluate(parse("1")), is(1));
    }

    @Test
    public void test_negate() throws Parser.Error, Eval.Error {
        assertThat(Eval.evaluate(parse("-1")), is(-1));
    }

    @Test
    public void test_plus() throws Parser.Error, Eval.Error {
        assertThat(Eval.evaluate(parse("1 + 2")), is(3));
    }

    @Test
    public void test_minus() throws Parser.Error, Eval.Error {
        assertThat(Eval.evaluate(parse("1 - 2")), is(-1));
    }

    @Test
    public void test_times() throws Parser.Error, Eval.Error {
        assertThat(Eval.evaluate(parse("3 * 2")), is(6));
    }

    @Test
    public void test_divide() throws Parser.Error, Eval.Error {
        assertThat(Eval.evaluate(parse("8 / 4")), is(2));
    }

    @Test(expected=Eval.Error.class)
    public void test_divide_by_zero() throws Parser.Error, Eval.Error {
        Eval.evaluate(parse("8 / 0"));
    }

    @Test(expected=Eval.Error.class)
    public void test_bad_op() throws Parser.Error, Eval.Error {
        Eval.evaluate((new Node("wizard")).addkid(new Node(3)).addkid(new Node(4)));
    }

    @Test(expected=Eval.Error.class)
    public void test_too_few_kids() throws Parser.Error, Eval.Error {
        Eval.evaluate((new Node("+")).addkid(new Node(4)));
    }
}
