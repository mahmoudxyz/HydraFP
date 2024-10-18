package hydrafp.io.core.adt;

import hydrafp.io.core.functions.Function1;
import hydrafp.io.core.functions.Function2;

import java.util.Objects;


public final class Pair<A, B> {
    private final A first;
    private final B second;

    private Pair(A first, B second) {
        this.first = first;
        this.second = second;
    }

    public static <A, B> Pair<A, B> of(A first, B second) {
        return new Pair<>(first, second);
    }

    public A first() {
        return first;
    }

    public B second() {
        return second;
    }

    public <C> Either<String, Pair<C, B>> mapFirst(Function1<? super A, ? extends C> mapper) {
        if (mapper == null) {
            return Either.left("Mapper function cannot be null");
        }
        return Either.right(new Pair<>(mapper.apply(first), second));
    }

    public <C> Either<String, Pair<A, C>> mapSecond(Function1<? super B, ? extends C> mapper) {
        if (mapper == null) {
            return Either.left("Mapper function cannot be null");
        }
        return Either.right(new Pair<>(first, mapper.apply(second)));
    }

    public <C, D> Either<String, Pair<C, D>> bimap(Function1<? super A, ? extends C> firstMapper,
                                                   Function1<? super B, ? extends D> secondMapper) {
        if (firstMapper == null || secondMapper == null) {
            return Either.left("Mapper functions cannot be null");
        }
        return Either.right(new Pair<>(firstMapper.apply(first), secondMapper.apply(second)));
    }

    public <C> Either<String, C> fold(Function2<? super A, ? super B, ? extends C> mapper) {
        if (mapper == null) {
            return Either.left("Mapper function cannot be null");
        }
        return Either.right(mapper.apply(first, second));
    }

    public Pair<B, A> swap() {
        return new Pair<>(second, first);
    }

    public <C> Pair<Pair<A, C>, B> nestFirst(C value) {
        return new Pair<>(new Pair<>(first, value), second);
    }

    public <C> Pair<A, Pair<B, C>> nestSecond(C value) {
        return new Pair<>(first, new Pair<>(second, value));
    }

    public static <A, B, C> Function1<Pair<A, B>, Either<String, C>> uncurry(Function2<A, B, C> f) {
        if (f == null) {
            return pair -> Either.left("Function1 cannot be null");
        }
        return pair -> Either.right(f.apply(pair.first, pair.second));
    }

    public static <A, B, C> Either<String, Function2<A, B, C>> curry(Function1<Pair<A, B>, C> f) {
        if (f == null) {
            return Either.left("Function1 cannot be null");
        }
        return Either.right((a, b) -> f.apply(new Pair<>(a, b)));
    }

    public <C, D> Either<String, Pair<C, D>> flatMap(Function1<? super A, ? extends Pair<C, D>> f) {
        if (f == null) {
            return Either.left("Mapper function cannot be null");
        }
        return Either.right(f.apply(first));
    }

    public static <A> Pair<A, A> duplicate(A value) {
        return new Pair<>(value, value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pair<?, ?> pair = (Pair<?, ?>) o;
        return Objects.equals(first, pair.first) &&
                Objects.equals(second, pair.second);
    }

    @Override
    public int hashCode() {
        return Objects.hash(first, second);
    }

    @Override
    public String toString() {
        return "Pair(" + first + ", " + second + ")";
    }
}