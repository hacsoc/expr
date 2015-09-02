package cwru.hacsoc.expr;

import java.util.List;

class Eval {

    public static class Error extends Exception {
        public Error(String msg) {
            super(msg);
        }
    }

    public static int evaluate(Node n) throws Error {
        if (n.kids.size() == 0) {
            Node<Integer> i = (Node<Integer>)n;
            return i.label;
        }
        return apply((Node<String>)n);
    }

    static int apply(Node<String> node) throws Error {
        if (node.kids.size() == 1 && node.label.equals("-")) {
            return - evaluate(node.kids.get(0));
        } if (node.kids.size() < 2) {
            throw new Error(
              String.format(
                "Not enough children to apply the op to %s", node));
        }
        int acc = evaluate(node.kids.get(0));
        for (int i = 1; i < node.kids.size(); i++) {
            acc = apply_op(node.label, acc, evaluate(node.kids.get(i)));
        }
        return acc;
    }

    static int apply_op(String op, int left, int right) throws Error {
        switch (op) {
        case "+":
            return left + right;
        case "-":
            return left - right;
        case "*":
            return left * right;
        case "/":
            if (right == 0) {
                throw new Error("Divide by zero");
            }
            return left / right;
        default:
            throw new Error(String.format("Unexpected operator %s", op));
        }
    }
}
