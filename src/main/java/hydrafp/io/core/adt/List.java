package hydrafp.io.core.adt;

import java.util.function.Function;
import java.util.function.Predicate;

public abstract class List<A> {
    private List() {}

    public abstract <B> List<B> map(Function<? super A, ? extends B> f);
    public abstract List<A> filter(Predicate<? super A> p);
    public abstract <B> B foldLeft(B initial, Function<B, Function<A, B>> f);
    public abstract boolean isEmpty();
    public abstract int size();

    public static <A> List<A> empty() {
        return new Nil<>();
    }

    public static <A> List<A> of(A... elements) {
        List<A> result = empty();
        for (int i = elements.length - 1; i >= 0; i--) {
            result = new Cons<>(elements[i], result);
        }
        return result;
    }

    private static class Nil<A> extends List<A> {
        @Override
        public <B> List<B> map(Function<? super A, ? extends B> f) {
            return empty();
        }

        @Override
        public List<A> filter(Predicate<? super A> p) {
            return this;
        }

        @Override
        public <B> B foldLeft(B initial, Function<B, Function<A, B>> f) {
            return initial;
        }

        @Override
        public boolean isEmpty() {
            return true;
        }

        @Override
        public int size() {
            return 0;
        }

        @Override
        public String toString() {
            return "Nil";
        }
    }

    public static class Cons<A> extends List<A> {
        private final A head;
        private final List<A> tail;

        Cons(A head, List<A> tail) {
            this.head = head;
            this.tail = tail;
        }

        @Override
        public <B> List<B> map(Function<? super A, ? extends B> f) {
            return new Cons<>(f.apply(head), tail.map(f));
        }

        @Override
        public List<A> filter(Predicate<? super A> p) {
            if (p.test(head)) {
                return new Cons<>(head, tail.filter(p));
            } else {
                return tail.filter(p);
            }
        }

        @Override
        public <B> B foldLeft(B initial, Function<B, Function<A, B>> f) {
            return tail.foldLeft(f.apply(initial).apply(head), f);
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public int size() {
            return 1 + tail.size();
        }

        @Override
        public String toString() {
            return "Cons(" + head + ", " + tail + ")";
        }
    }
}