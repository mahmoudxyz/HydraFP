package hydrafp.io.core.functions;

@FunctionalInterface
public interface Function1<A, R> {
    R apply(A a);

    default <V> Function1<A, V> andThen(Function1<? super R, ? extends V> after) {
        return (A a) -> after.apply(apply(a));
    }

    default <V> Function1<V, R> compose(Function1<? super V, ? extends A> before) {
        return (V v) -> apply(before.apply(v));
    }

    static <T> Function1<T, T> identity() {
        return t -> t;
    }
}