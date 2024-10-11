package hydrafp.io.core.adt;

import java.util.function.Function;

public class NonEmptyList<A> {
    private final A head;
    private final List<A> tail;

    private NonEmptyList(A head, List<A> tail) {
        this.head = head;
        this.tail = tail;
    }

    public static <A> NonEmptyList<A> of(A head, A... tail) {
        return new NonEmptyList<>(head, List.of(tail));
    }

    public A head() {
        return head;
    }

    public List<A> tail() {
        return tail;
    }

    public <B> NonEmptyList<B> map(Function<? super A, ? extends B> f) {
        return new NonEmptyList<>(f.apply(head), tail.map(f));
    }

    public List<A> toList() {
        return List.of(head).foldLeft(tail, acc -> elem -> new List.Cons<>(elem, acc));
    }

    @Override
    public String toString() {
        return "NonEmptyList(" + head + ", " + tail + ")";
    }
}