package hydrafp.io.core.adt;

import java.util.Objects;
import java.util.function.Function;

public final class Triple<A, B, C> {
    private final A first;
    private final B second;
    private final C third;

    private Triple(A first, B second, C third) {
        this.first = first;
        this.second = second;
        this.third = third;
    }

    public static <A, B, C> Triple<A, B, C> of(A first, B second, C third) {
        return new Triple<>(first, second, third);
    }

    public A first() {
        return first;
    }

    public B second() {
        return second;
    }

    public C third() {
        return third;
    }

    public <D> Either<String, Triple<D, B, C>> mapFirst(Function<? super A, ? extends D> mapper) {
        if (mapper == null) {
            return Either.left("Mapper function cannot be null");
        }
        return Either.right(new Triple<>(mapper.apply(first), second, third));
    }

    public <D> Either<String, Triple<A, D, C>> mapSecond(Function<? super B, ? extends D> mapper) {
        if (mapper == null) {
            return Either.left("Mapper function cannot be null");
        }
        return Either.right(new Triple<>(first, mapper.apply(second), third));
    }

    public <D> Either<String, Triple<A, B, D>> mapThird(Function<? super C, ? extends D> mapper) {
        if (mapper == null) {
            return Either.left("Mapper function cannot be null");
        }
        return Either.right(new Triple<>(first, second, mapper.apply(third)));
    }

    public <D, E, F> Either<String, Triple<D, E, F>> trimap(Function<? super A, ? extends D> firstMapper,
                                                            Function<? super B, ? extends E> secondMapper,
                                                            Function<? super C, ? extends F> thirdMapper) {
        if (firstMapper == null || secondMapper == null || thirdMapper == null) {
            return Either.left("Mapper functions cannot be null");
        }
        return Either.right(new Triple<>(firstMapper.apply(first), secondMapper.apply(second), thirdMapper.apply(third)));
    }

    public <D> Either<String, D> fold(Function3<? super A, ? super B, ? super C, ? extends D> mapper) {
        if (mapper == null) {
            return Either.left("Mapper function cannot be null");
        }
        return Either.right(mapper.apply(first, second, third));
    }

    public Triple<B, C, A> rotateLeft() {
        return new Triple<>(second, third, first);
    }

    public Triple<C, A, B> rotateRight() {
        return new Triple<>(third, first, second);
    }

    public Pair<A, Pair<B, C>> toPair() {
        return Pair.of(first, Pair.of(second, third));
    }

    public static <A, B, C> Triple<A, B, C> fromPair(Pair<A, Pair<B, C>> pair) {
        return new Triple<>(pair.first(), pair.second().first(), pair.second().second());
    }

    public static <A, B, C, D> Function<Triple<A, B, C>, Either<String, D>> uncurry(Function3<A, B, C, D> f) {
        if (f == null) {
            return x -> Either.left("Function cannot be null");
        }
        return triple -> Either.right(f.apply(triple.first, triple.second, triple.third));
    }

    public static <A, B, C, D> Either<String, Function3<A, B, C, D>> curry(Function<Triple<A, B, C>, D> f) {
        if (f == null) {
            return Either.left("Function cannot be null");
        }
        return Either.right((a, b, c) -> f.apply(new Triple<>(a, b, c)));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Triple<?, ?, ?> triple = (Triple<?, ?, ?>) o;
        return Objects.equals(first, triple.first) &&
                Objects.equals(second, triple.second) &&
                Objects.equals(third, triple.third);
    }

    @Override
    public int hashCode() {
        return Objects.hash(first, second, third);
    }

    @Override
    public String toString() {
        return "Triple(" + first + ", " + second + ", " + third + ")";
    }

    @FunctionalInterface
    public interface Function3<T1, T2, T3, R> {
        R apply(T1 t1, T2 t2, T3 t3);
    }
}