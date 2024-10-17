package hydrafp.io.core.tagged;

import hydrafp.io.core.adt.Try;
import hydrafp.io.core.adt.Either;
import hydrafp.io.core.adt.Option;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.Consumer;

public final class Tagged<T, V> {
    private final V value;

    private Tagged(V value) {
        this.value = Objects.requireNonNull(value, "Value cannot be null");
    }


    public static <T, V> Tagged<T, V> of(V value) {
        return new Tagged<>(value);
    }

    public static <T, V> Option<Tagged<T, V>> fromOption(Option<V> option) {
        return option.map(Tagged::of);
    }

    public static <T, V> Try<Tagged<T, V>> fromTry(Try<V> t) {
        return t.map(Tagged::of);
    }

    public static <T, L, V> Either<L, Tagged<T, V>> fromEither(Either<L, V> either) {
        return either.map(Tagged::of);
    }

    public V getValue() {
        return value;
    }

    public <R> Tagged<T, R> map(Function<? super V, ? extends R> mapper) {
        return Tagged.of(mapper.apply(value));
    }

    public <R> Tagged<R, V> retag() {
        return Tagged.of(value);
    }

    public <R> R fold(Function<? super V, ? extends R> mapper) {
        return mapper.apply(value);
    }

    public Option<V> toOption() {
        return Option.some(value);
    }

    public Try<V> toTry() {
        return Try.success(value);
    }

    public <L> Either<L, V> toEither(Supplier<L> leftSupplier) {
        return Either.right(value);
    }

    public Tagged<T, V> filter(Predicate<? super V> predicate) {
        return predicate.test(value) ? this : null;
    }

    public Option<Tagged<T, V>> filterToOption(Predicate<? super V> predicate) {
        return predicate.test(value) ? Option.some(this) : Option.none();
    }

    public <R> Tagged<T, R> flatMap(Function<? super V, Tagged<T, R>> mapper) {
        return mapper.apply(value);
    }

    public <R> R match(Function<? super V, ? extends R> matcher) {
        return matcher.apply(value);
    }

    public Tagged<T, V> peek(Consumer<? super V> action) {
        action.accept(value);
        return this;
    }

    public <E extends Throwable> Tagged<T, V> peekOrThrow(CheckedConsumer<? super V, E> action) throws E {
        action.accept(value);
        return this;
    }

    public <U> Tagged<T, U> ap(Tagged<T, Function<V, U>> taggedFunction) {
        return map(taggedFunction.getValue());
    }

    public <U, R> Tagged<T, R> zip(Tagged<T, U> other, BiFunction<V, U, R> zipper) {
        return Tagged.of(zipper.apply(this.value, other.value));
    }

    public static <T, V1, V2, R> Tagged<T, R> map2(
            Tagged<T, V1> t1,
            Tagged<T, V2> t2,
            BiFunction<V1, V2, R> f) {
        return t1.zip(t2, f);
    }


    public static <T, V> Tagged<T, V> pure(V value) {
        return Tagged.of(value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tagged<?, ?> tagged = (Tagged<?, ?>) o;
        return Objects.equals(value, tagged.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "Tagged[" + value + "]";
    }

    @FunctionalInterface
    public interface CheckedConsumer<T, E extends Throwable> {
        void accept(T t) throws E;
    }

    @FunctionalInterface
    public interface BiFunction<T, U, R> {
        R apply(T t, U u);
    }
}