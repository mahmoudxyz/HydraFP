package hydrafp.io.core.adt;

import java.util.function.Function;

public abstract class Tree<A> {
    private Tree() {}

    public abstract <B> Tree<B> map(Function<A, B> f);
    public abstract int size();
    public abstract int depth();
    public abstract A getValue();

    public static <A> Tree<A> leaf(A value) {
        return new Leaf<>(value);
    }

    public static <A> Tree<A> node(A value, Tree<A> left, Tree<A> right) {
        return new Node<>(value, left, right);
    }

    private static final class Leaf<A> extends Tree<A> {
        private final A value;

        private Leaf(A value) {
            this.value = value;
        }

        @Override
        public <B> Tree<B> map(Function<A, B> f) {
            return new Leaf<>(f.apply(value));
        }

        @Override
        public int size() {
            return 1;
        }

        @Override
        public int depth() {
            return 1;
        }

        @Override
        public A getValue() {
            return value;
        }

        @Override
        public String toString() {
            return "Leaf(" + value + ")";
        }
    }

    private static final class Node<A> extends Tree<A> {
        private final A value;
        private final Tree<A> left;
        private final Tree<A> right;

        private Node(A value, Tree<A> left, Tree<A> right) {
            this.value = value;
            this.left = left;
            this.right = right;
        }

        @Override
        public <B> Tree<B> map(Function<A, B> f) {
            return new Node<>(f.apply(value), left.map(f), right.map(f));
        }

        @Override
        public int size() {
            return 1 + left.size() + right.size();
        }

        @Override
        public int depth() {
            return 1 + Math.max(left.depth(), right.depth());
        }

        @Override
        public A getValue() {
            return value;
        }

        @Override
        public String toString() {
            return "Node(" + value + ", " + left + ", " + right + ")";
        }
    }
}