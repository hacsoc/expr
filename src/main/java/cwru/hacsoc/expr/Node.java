package cwru.hacsoc.expr;

import java.util.ArrayList;

public class Node<T> {

    public T label;
    public ArrayList<Node<?>> kids = new ArrayList<Node<?>>();

    public Node(T label) {
        this.label = label;
    }

    public Node<T> addkid(Node<?> n) {
        this.kids.add(n);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Node) {
            return this.equals((Node)o);
        }
        return false;
    }

    public boolean equals(Node n) {
        if (!this.label.equals(n.label)) {
            return false;
        }
        if (this.kids.size() != n.kids.size()) {
            return false;
        }
        for (int i = 0; i < this.kids.size(); i++) {
            if (!this.kids.get(i).equals(n.kids.get(i))) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        int max = Integer.MAX_VALUE;
        int hash = (2 * (label.hashCode() + 1)) % max;
        int i = 3;
        for (Node n : this.kids) {
            hash = (hash + ((i * (n.hashCode() + 1)) % max)) % max;
            i += 2;
        }
        return hash;
    }

    @Override
    public String toString() {
        if (kids.size() == 0) {
            return label.toString();
        }
        StringBuilder sb = new StringBuilder();
        for (Node kid : kids) {
            sb.append(" ");
            sb.append(kid);
        }
        return String.format("(%s%s)", label, sb);
    }
}

