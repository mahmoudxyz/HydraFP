package hydrafp.io.core.functions;

import hydrafp.io.core.adt.Pair;
import hydrafp.io.core.adt.Triple;

@FunctionalInterface
public interface Function4<A, B, C, D, R> {
    R apply(A a, B b, C c, D d);

    default <V> Function4<A, B, C, D, V> andThen(Function1<? super R, ? extends V> after) {
        return (a, b, c, d) -> after.apply(apply(a, b, c, d));
    }

    default Function3<B, C, D, R> partial(A a) {
        return (b, c, d) -> apply(a, b, c, d);
    }

    default Function2<C, D, R> partial(A a, B b) {
        return (c, d) -> apply(a, b, c, d);
    }

    default Function1<D, R> partial(A a, B b, C c) {
        return d -> apply(a, b, c, d);
    }

    default Function1<Pair<A, Triple<B, C, D>>, R> tupled() {
        return p -> apply(p.first(), p.second().first(), p.second().second(), p.second().third());
    }

    static <A, B, C, D, R> Function4<A, B, C, D, R> fromPairTriple(Function1<Pair<A, Triple<B, C, D>>, R> f) {
        return (a, b, c, d) -> f.apply(Pair.of(a, Triple.of(b, c, d)));
    }
}