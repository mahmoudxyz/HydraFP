package hydrafp.io.core.functions;

import hydrafp.io.core.adt.Triple;

@FunctionalInterface
public interface Function3<A, B, C, R> {
    R apply(A a, B b, C c);

    default <V> Function3<A, B, C, V> andThen(Function1<? super R, ? extends V> after) {
        return (a, b, c) -> after.apply(apply(a, b, c));
    }

    default Function2<B, C, R> partial(A a) {
        return (b, c) -> apply(a, b, c);
    }

    default Function1<C, R> partial(A a, B b) {
        return c -> apply(a, b, c);
    }

    default Function1<Triple<A, B, C>, R> tupled() {
        return t -> apply(t.first(), t.second(), t.third());
    }

    static <A, B, C, R> Function3<A, B, C, R> fromTriple(Function1<Triple<A, B, C>, R> f) {
        return (a, b, c) -> f.apply(Triple.of(a, b, c));
    }
}