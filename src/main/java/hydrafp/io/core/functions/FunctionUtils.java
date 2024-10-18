package hydrafp.io.core.functions;

import hydrafp.io.core.adt.Pair;
import hydrafp.io.core.adt.Triple;
import hydrafp.io.core.adt.Try;
import hydrafp.io.core.adt.Option;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public final class FunctionUtils {

    private FunctionUtils() {
        throw new AssertionError("No instances");
    }

    // Function1 utilities
    public static <A, R> Function1<A, R> memoize(Function1<A, R> f) {
        Map<A, R> cache = new ConcurrentHashMap<>();
        return a -> cache.computeIfAbsent(a, f::apply);
    }

    public static <A, B, R> Function1<A, Function1<B, R>> curry(Function2<A, B, R> f) {
        return a -> b -> f.apply(a, b);
    }

    public static <A, B, R> Function2<A, B, R> uncurry(Function1<A, Function1<B, R>> f) {
        return (a, b) -> f.apply(a).apply(b);
    }

    public static <A, R> Function1<A, Try<R>> lift(Function1<A, R> f) {
        return a -> Try.of(() -> f.apply(a));
    }

    public static <A, R> Function1<A, Option<R>> liftOption(Function1<A, R> f) {
        return a -> Option.of(f.apply(a));
    }

    // Function2 utilities
    public static <A, B, R> Function2<A, B, R> memoize2(Function2<A, B, R> f) {
        Map<Pair<A, B>, R> cache = new ConcurrentHashMap<>();
        return (a, b) -> cache.computeIfAbsent(Pair.of(a, b), p -> f.apply(p.first(), p.second()));
    }

    public static <A, B, R> Function1<Pair<A, B>, R> tuple2(Function2<A, B, R> f) {
        return p -> f.apply(p.first(), p.second());
    }

    public static <A, B, R> Function2<A, B, R> detuple2(Function1<Pair<A, B>, R> f) {
        return (a, b) -> f.apply(Pair.of(a, b));
    }

    public static <A, B, R> Function2<A, B, Try<R>> lift2(Function2<A, B, R> f) {
        return (a, b) -> Try.of(() -> f.apply(a, b));
    }

    public static <A, B, R> Function2<A, B, Option<R>> liftOption2(Function2<A, B, R> f) {
        return (a, b) -> Option.of(f.apply(a, b));
    }

    // Function3 utilities
    public static <A, B, C, R> Function3<A, B, C, R> memoize3(Function3<A, B, C, R> f) {
        Map<Triple<A, B, C>, R> cache = new ConcurrentHashMap<>();
        return (a, b, c) -> cache.computeIfAbsent(Triple.of(a, b, c), t -> f.apply(t.first(), t.second(), t.third()));
    }

    public static <A, B, C, R> Function1<Triple<A, B, C>, R> tuple3(Function3<A, B, C, R> f) {
        return t -> f.apply(t.first(), t.second(), t.third());
    }

    public static <A, B, C, R> Function3<A, B, C, R> detuple3(Function1<Triple<A, B, C>, R> f) {
        return (a, b, c) -> f.apply(Triple.of(a, b, c));
    }

    public static <A, B, C, R> Function3<A, B, C, Try<R>> lift3(Function3<A, B, C, R> f) {
        return (a, b, c) -> Try.of(() -> f.apply(a, b, c));
    }

    public static <A, B, C, R> Function3<A, B, C, Option<R>> liftOption3(Function3<A, B, C, R> f) {
        return (a, b, c) -> Option.of(f.apply(a, b, c));
    }

    // Function4 utilities
    public static <A, B, C, D, R> Function4<A, B, C, D, R> memoize4(Function4<A, B, C, D, R> f) {
        Map<Pair<A, Triple<B, C, D>>, R> cache = new ConcurrentHashMap<>();
        return (a, b, c, d) -> cache.computeIfAbsent(Pair.of(a, Triple.of(b, c, d)),
                p -> f.apply(p.first(), p.second().first(), p.second().second(), p.second().third()));
    }

    public static <A, B, C, D, R> Function1<Pair<A, Triple<B, C, D>>, R> tuple4(Function4<A, B, C, D, R> f) {
        return p -> f.apply(p.first(), p.second().first(), p.second().second(), p.second().third());
    }

    public static <A, B, C, D, R> Function4<A, B, C, D, R> detuple4(Function1<Pair<A, Triple<B, C, D>>, R> f) {
        return (a, b, c, d) -> f.apply(Pair.of(a, Triple.of(b, c, d)));
    }

    public static <A, B, C, D, R> Function4<A, B, C, D, Try<R>> lift4(Function4<A, B, C, D, R> f) {
        return (a, b, c, d) -> Try.of(() -> f.apply(a, b, c, d));
    }

    public static <A, B, C, D, R> Function4<A, B, C, D, Option<R>> liftOption4(Function4<A, B, C, D, R> f) {
        return (a, b, c, d) -> Option.of(f.apply(a, b, c, d));
    }

    // Function5 utilities
    public static <A, B, C, D, E, R> Function5<A, B, C, D, E, R> memoize5(Function5<A, B, C, D, E, R> f) {
        Map<Pair<Pair<A, B>, Triple<C, D, E>>, R> cache = new ConcurrentHashMap<>();
        return (a, b, c, d, e) -> cache.computeIfAbsent(Pair.of(Pair.of(a, b), Triple.of(c, d, e)),
                p -> f.apply(p.first().first(), p.first().second(),
                        p.second().first(), p.second().second(), p.second().third()));
    }

    public static <A, B, C, D, E, R> Function1<Pair<Pair<A, B>, Triple<C, D, E>>, R> tuple5(Function5<A, B, C, D, E, R> f) {
        return p -> f.apply(p.first().first(), p.first().second(), p.second().first(), p.second().second(), p.second().third());
    }

    public static <A, B, C, D, E, R> Function5<A, B, C, D, E, R> detuple5(Function1<Pair<Pair<A, B>, Triple<C, D, E>>, R> f) {
        return (a, b, c, d, e) -> f.apply(Pair.of(Pair.of(a, b), Triple.of(c, d, e)));
    }

    public static <A, B, C, D, E, R> Function5<A, B, C, D, E, Try<R>> lift5(Function5<A, B, C, D, E, R> f) {
        return (a, b, c, d, e) -> Try.of(() -> f.apply(a, b, c, d, e));
    }

    public static <A, B, C, D, E, R> Function5<A, B, C, D, E, Option<R>> liftOption5(Function5<A, B, C, D, E, R> f) {
        return (a, b, c, d, e) -> Option.of(f.apply(a, b, c, d, e));
    }

    // Additional utility methods
    public static <A, B, C, R> Function1<A, Function1<B, Function1<C, R>>> curry3(Function3<A, B, C, R> f) {
        return a -> b -> c -> f.apply(a, b, c);
    }

    public static <A, B, C, D, R> Function1<A, Function1<B, Function1<C, Function1<D, R>>>> curry4(Function4<A, B, C, D, R> f) {
        return a -> b -> c -> d -> f.apply(a, b, c, d);
    }

    public static <A, B, C, D, E, R> Function1<A, Function1<B, Function1<C, Function1<D, Function1<E, R>>>>> curry5(Function5<A, B, C, D, E, R> f) {
        return a -> b -> c -> d -> e -> f.apply(a, b, c, d, e);
    }

    public static <A, B, C, R> Function3<A, B, C, R> uncurry3(Function1<A, Function1<B, Function1<C, R>>> f) {
        return (a, b, c) -> f.apply(a).apply(b).apply(c);
    }

    public static <A, B, C, D, R> Function4<A, B, C, D, R> uncurry4(Function1<A, Function1<B, Function1<C, Function1<D, R>>>> f) {
        return (a, b, c, d) -> f.apply(a).apply(b).apply(c).apply(d);
    }

    public static <A, B, C, D, E, R> Function5<A, B, C, D, E, R> uncurry5(Function1<A, Function1<B, Function1<C, Function1<D, Function1<E, R>>>>> f) {
        return (a, b, c, d, e) -> f.apply(a).apply(b).apply(c).apply(d).apply(e);
    }
}