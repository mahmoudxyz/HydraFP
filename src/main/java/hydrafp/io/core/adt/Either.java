package hydrafp.io.core.adt;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public abstract class Either<L, R> {
    private Either() {
    }

    public static <L, R> Either<L, R> left(L value) {
        return new Left<>(Objects.requireNonNull(value, "Left value must not be null"));
    }

    public static <L, R> Either<L, R> right(R value) {
        return new Right<>(Objects.requireNonNull(value, "Right value must not be null"));
    }

    public abstract <T> T fold(Function<? super L, ? extends T> leftMapper, Function<? super R, ? extends T> rightMapper);

    public abstract <T> Either<L, T> map(Function<? super R, ? extends T> mapper);

    public abstract <T> Either<L, T> flatMap(Function<? super R, ? extends Either<L, T>> mapper);

    public abstract <T> Either<T, R> mapLeft(Function<? super L, ? extends T> mapper);

    public abstract <T> Either<T, R> flatMapLeft(Function<? super L, ? extends Either<T, R>> mapper);

    public abstract Either<L, R> filter(Predicate<? super R> predicate, Supplier<? extends L> leftSupplier);

    public abstract Either<L, R> filterOrElse(Predicate<? super R> predicate, Function<? super R, ? extends L> leftMapper);

    public abstract boolean isLeft();

    public abstract boolean isRight();

    public abstract L getLeft();

    public abstract R getRight();

    public abstract Optional<L> toLeftOptional();

    public abstract Optional<R> toRightOptional();

    public abstract Either<R, L> swap();

    public R getRightOrElse(R defaultValue) {
        return fold(l -> defaultValue, Function.identity());
    }

    public L getLeftOrElse(L defaultValue) {
        return fold(Function.identity(), r -> defaultValue);
    }

    public R getRightOrElse(Supplier<? extends R> supplier) {
        return fold(l -> supplier.get(), Function.identity());
    }

    public L getLeftOrElse(Supplier<? extends L> supplier) {
        return fold(Function.identity(), r -> supplier.get());
    }

    public abstract Either<L, R> orElse(Supplier<? extends Either<L, R>> supplier);

    public abstract Either<L, R> peek(Consumer<? super R> action);

    public abstract Either<L, R> peekLeft(Consumer<? super L> action);

    public abstract Option<R> toOption();

    public abstract Try<R> toTry();

    public static <L, R> Either<L, R> fromOption(Option<R> option, Supplier<L> leftSupplier) {
        Objects.requireNonNull(option, "option must not be null");
        Objects.requireNonNull(leftSupplier, "leftSupplier must not be null");
        return option.map(Either::<L, R>right)
                .getOrElse(() -> Either.left(leftSupplier.get()));
    }

    public static <L, R> Either<L, R> fromTry(Try<R> t, Function<Throwable, L> leftMapper) {
        Objects.requireNonNull(t, "try must not be null");
        Objects.requireNonNull(leftMapper, "leftMapper must not be null");
        return t.fold(
                ex -> Either.left(leftMapper.apply(ex)),
                Either::right
        );
    }


    private static final class Left<L, R> extends Either<L, R> {
        private final L value;

        private Left(L value) {
            this.value = value;
        }

        @Override
        public <T> T fold(Function<? super L, ? extends T> leftMapper, Function<? super R, ? extends T> rightMapper) {
            return leftMapper.apply(value);
        }

        @Override
        public <T> Either<L, T> map(Function<? super R, ? extends T> mapper) {
            return (Either<L, T>) this;
        }

        @Override
        public <T> Either<L, T> flatMap(Function<? super R, ? extends Either<L, T>> mapper) {
            return (Either<L, T>) this;
        }

        @Override
        public <T> Either<T, R> mapLeft(Function<? super L, ? extends T> mapper) {
            return new Left<>(mapper.apply(value));
        }

        @Override
        public <T> Either<T, R> flatMapLeft(Function<? super L, ? extends Either<T, R>> mapper) {
            return mapper.apply(value);
        }

        @Override
        public Either<L, R> filter(Predicate<? super R> predicate, Supplier<? extends L> leftSupplier) {
            return this;
        }

        @Override
        public Either<L, R> filterOrElse(Predicate<? super R> predicate, Function<? super R, ? extends L> leftMapper) {
            return this;
        }

        @Override
        public boolean isLeft() {
            return true;
        }

        @Override
        public boolean isRight() {
            return false;
        }

        @Override
        public L getLeft() {
            return value;
        }

        @Override
        public R getRight() {
            throw new NoSuchElementException("Cannot get right value from Left");
        }

        @Override
        public Optional<L> toLeftOptional() {
            return Optional.of(value);
        }

        @Override
        public Optional<R> toRightOptional() {
            return Optional.empty();
        }

        @Override
        public Either<R, L> swap() {
            return new Right<>(value);
        }

        @Override
        public Either<L, R> orElse(Supplier<? extends Either<L, R>> supplier) {
            Objects.requireNonNull(supplier, "supplier must not be null");
            return supplier.get();
        }

        @Override
        public Either<L, R> peek(Consumer<? super R> action) {
            return this;
        }

        @Override
        public Either<L, R> peekLeft(Consumer<? super L> action) {
            action.accept(value);
            return this;
        }

        @Override
        public Option<R> toOption() {
            return Option.none();
        }

        @Override
        public Try<R> toTry() {
            return Try.failure(new NoSuchElementException("Either.Left"));
        }


        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            Left<?, ?> left = (Left<?, ?>) obj;
            return Objects.equals(value, left.value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(value);
        }

        @Override
        public String toString() {
            return "Left(" + value + ")";
        }
    }

    private static final class Right<L, R> extends Either<L, R> {
        private final R value;

        private Right(R value) {
            this.value = value;
        }

        @Override
        public <T> T fold(Function<? super L, ? extends T> leftMapper, Function<? super R, ? extends T> rightMapper) {
            return rightMapper.apply(value);
        }

        @Override
        public <T> Either<L, T> map(Function<? super R, ? extends T> mapper) {
            return new Right<>(mapper.apply(value));
        }

        @Override
        public <T> Either<L, T> flatMap(Function<? super R, ? extends Either<L, T>> mapper) {
            return mapper.apply(value);
        }

        @Override
        public <T> Either<T, R> mapLeft(Function<? super L, ? extends T> mapper) {
            return (Either<T, R>) this;
        }

        @Override
        public <T> Either<T, R> flatMapLeft(Function<? super L, ? extends Either<T, R>> mapper) {
            return (Either<T, R>) this;
        }

        @Override
        public Either<L, R> filter(Predicate<? super R> predicate, Supplier<? extends L> leftSupplier) {
            return predicate.test(value) ? this : new Left<>(leftSupplier.get());
        }

        @Override
        public Either<L, R> filterOrElse(Predicate<? super R> predicate, Function<? super R, ? extends L> leftMapper) {
            return predicate.test(value) ? this : new Left<>(leftMapper.apply(value));
        }

        @Override
        public boolean isLeft() {
            return false;
        }

        @Override
        public boolean isRight() {
            return true;
        }

        @Override
        public L getLeft() {
            throw new NoSuchElementException("Cannot get left value from Right");
        }

        @Override
        public R getRight() {
            return value;
        }

        @Override
        public Optional<L> toLeftOptional() {
            return Optional.empty();
        }

        @Override
        public Optional<R> toRightOptional() {
            return Optional.of(value);
        }

        @Override
        public Either<R, L> swap() {
            return new Left<>(value);
        }

        @Override
        public Either<L, R> orElse(Supplier<? extends Either<L, R>> supplier) {
            return this;
        }


        @Override
        public Either<L, R> peek(Consumer<? super R> action) {
            action.accept(value);
            return this;
        }

        @Override
        public Either<L, R> peekLeft(Consumer<? super L> action) {
            return this;
        }

        @Override
        public Option<R> toOption() {
            return Option.some(value);
        }

        @Override
        public Try<R> toTry() {
            return Try.success(value);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            Right<?, ?> right = (Right<?, ?>) obj;
            return Objects.equals(value, right.value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(value);
        }

        @Override
        public String toString() {
            return "Right(" + value + ")";
        }
    }
}