package hydrafp.io.core.functions;

import hydrafp.io.core.adt.Pair;
import hydrafp.io.core.adt.Triple;

@FunctionalInterface
public interface Function5<A, B, C, D, E, R> {
    R apply(A a, B b, C c, D d, E e);

    default <V> Function5<A, B, C, D, E, V> andThen(Function1<? super R, ? extends V> after) {
        return (a, b, c, d, e) -> after.apply(apply(a, b, c, d, e));
    }

    default Function4<B, C, D, E, R> partial(A a) {
        return (b, c, d, e) -> apply(a, b, c, d, e);
    }

    default Function3<C, D, E, R> partial(A a, B b) {
        return (c, d, e) -> apply(a, b, c, d, e);
    }

    default Function2<D, E, R> partial(A a, B b, C c) {
        return (d, e) -> apply(a, b, c, d, e);
    }

    default Function1<E, R> partial(A a, B b, C c, D d) {
        return e -> apply(a, b, c, d, e);
    }

    default Function1<Pair<Pair<A, B>, Triple<C, D, E>>, R> tupled() {
        return p -> apply(p.first().first(), p.first().second(), p.second().first(), p.second().second(), p.second().third());
    }

    static <A, B, C, D, E, R> Function5<A, B, C, D, E, R> fromPairPairTriple(Function1<Pair<Pair<A, B>, Triple<C, D, E>>, R> f) {
        return (a, b, c, d, e) -> f.apply(Pair.of(Pair.of(a, b), Triple.of(c, d, e)));
    }
}