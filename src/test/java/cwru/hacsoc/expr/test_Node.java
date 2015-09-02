package cwru.hacsoc.expr;

import static org.junit.Assert.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import org.junit.Test;
import org.junit.Ignore;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

public class test_Node {

    @Test
    public void test_equals() {
        Node<String> n1 = new Node<String>("hello");
        Node<String> n2 = new Node<String>("hello");
        assertThat(n1, is(n2));
        assertThat(n1.hashCode(), is(n2.hashCode()));
    }

    @Test
    public void test_kids_equals() {
        Node<String> n1 = (new Node<String>("hello"))
                            .addkid(new Node<String>("hello"));
        Node<String> n2 = (new Node<String>("hello"))
                            .addkid(new Node<String>("hello"));
        assertThat(n1, is(n2));
        assertThat(n1.hashCode(), is(n2.hashCode()));
    }

    @Test
    public void test_not_equals() {
        Node<String> n1 = new Node<String>("hello");
        Node<String> n2 = new Node<String>("hello-wat");
        assertThat(n1, is(not(n2)));
        assertThat(n1.hashCode(), is(not(n2.hashCode())));
    }

    @Test
    public void test_not_type_equals() {
        Node n1 = new Node<Float>(new Float(5.0));
        Node n2 = new Node<Integer>(new Integer(5));
        assertThat(n1, is(not(n2)));
        assertThat(n1.hashCode(), is(not(n2.hashCode())));
    }

    @Test
    public void test_not_kid_count_equals() {
        Node<String> n1 = new Node<String>("hello");
        Node<String> n2 = (new Node<String>("hello"))
                            .addkid(new Node<String>("hello"));
        assertThat(n1, is(not(n2)));
        assertThat(n1.hashCode(), is(not(n2.hashCode())));
    }

    @Test
    public void test_not_kid_label_equals() {
        Node<String> n1 = (new Node<String>("hello"))
                            .addkid(new Node<String>("hello-wizard"));
        Node<String> n2 = (new Node<String>("hello"))
                            .addkid(new Node<String>("hello"));
        assertThat(n1, is(not(n2)));
        assertThat(n1.hashCode(), is(not(n2.hashCode())));
    }
}

