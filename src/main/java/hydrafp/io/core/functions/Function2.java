package hydrafp.io.core.functions;


import hydrafp.io.core.adt.Pair;

@FunctionalInterface
public interface Function2<A, B, R> {
    R apply(A a, B b);

    default <V> Function2<A, B, V> andThen(Function1<? super R, ? extends V> after) {
        return (a, b) -> after.apply(apply(a, b));
    }

    default Function1<B, R> partial(A a) {
        return b -> apply(a, b);
    }

    default Function1<Pair<A, B>, R> tupled() {
        return pair -> apply(pair.first(), pair.second());
    }

    static <A, B, R> Function2<A, B, R> fromPair(Function1<Pair<A, B>, R> f) {
        return (a, b) -> f.apply(Pair.of(a, b));
    }
}
