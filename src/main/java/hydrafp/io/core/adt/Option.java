package hydrafp.io.core.adt;

import hydrafp.io.core.functions.Function1;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

public abstract class Option<T> {

    private Option() {
    } // Prevent direct instantiation

    public static <T> Option<T> of(T value) {
        return value == null ? none() : some(value);
    }

    public static <T> Option<T> some(T value) {
        return new Some<>(Objects.requireNonNull(value, "value must not be null"));
    }

    @SuppressWarnings("unchecked")
    public static <T> Option<T> none() {
        return (Option<T>) None.INSTANCE;
    }

    public abstract <U> Option<U> flatMap(Function1<? super T, Option<U>> mapper);

    public abstract <U> Option<U> map(Function1<? super T, ? extends U> mapper);

    public abstract T getOrElse(T other);

    public abstract T getOrElse(Supplier<? extends T> other);

    public abstract boolean isEmpty();

    public boolean isDefined() {
        return !isEmpty();
    }

    public abstract <L> Either<L, T> toEither(Supplier<L> leftSupplier);

    public abstract Option<T> filter(Predicate<? super T> predicate);

    public abstract <U> U match(Supplier<? extends U> noneCase, Function1<? super T, ? extends U> someCase);

    public abstract void forEach(Consumer<? super T> action);

    public abstract T get();

    public abstract Option<T> orElse(Supplier<? extends Option<T>> alternative);

    public abstract <X extends Throwable> T getOrElseThrow(Supplier<? extends X> exceptionSupplier) throws X;

    public abstract Stream<T> stream();

    public abstract Optional<T> toOptional();

    public static <T> Option<T> fromEither(Either<?, T> either) {
        Objects.requireNonNull(either, "either must not be null");
        return either.fold(l -> Option.none(), Option::some);
    }

    public Try<T> toTry(Supplier<? extends Throwable> ifNone) {
        return isDefined() ? Try.success(get()) : Try.failure(ifNone.get());
    }

    public static final class Some<T> extends Option<T> {
        private final T value;

        private Some(T value) {
            this.value = value;
        }

        @Override
        public T getOrElse(T other) {
            return value;
        }

        @Override
        public T getOrElse(Supplier<? extends T> other) {
            return value;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public <L> Either<L, T> toEither(Supplier<L> leftSupplier) {
            return Either.right(value);
        }


        @Override
        public <U> Option<U> map(Function1<? super T, ? extends U> mapper) {
            Objects.requireNonNull(mapper, "mapper must not be null");
            return Option.of(mapper.apply(value));
        }

        @Override
        public <U> Option<U> flatMap(Function1<? super T, Option<U>> mapper) {
            Objects.requireNonNull(mapper, "mapper must not be null");
            return mapper.apply(value);
        }

        @Override
        public Option<T> filter(Predicate<? super T> predicate) {
            Objects.requireNonNull(predicate, "predicate must not be null");
            return predicate.test(value) ? this : none();
        }

        @Override
        public <U> U match(Supplier<? extends U> noneCase, Function1<? super T, ? extends U> someCase) {
            Objects.requireNonNull(someCase, "someCase must not be null");
            return someCase.apply(value);
        }


        @Override
        public void forEach(Consumer<? super T> action) {
            Objects.requireNonNull(action, "action must not be null");
            action.accept(value);
        }

        @Override
        public T get() {
            return value;
        }

        @Override
        public Option<T> orElse(Supplier<? extends Option<T>> alternative) {
            return this;
        }

        @Override
        public <X extends Throwable> T getOrElseThrow(Supplier<? extends X> exceptionSupplier) {
            return value;
        }

        @Override
        public Stream<T> stream() {
            return Stream.of(value);
        }

        @Override
        public Optional<T> toOptional() {
            return Optional.of(value);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            Some<?> some = (Some<?>) obj;
            return Objects.equals(value, some.value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(value);
        }

        @Override
        public String toString() {
            return "Some(" + value + ")";
        }
    }

    public static final class None<T> extends Option<T> {
        private static final None<?> INSTANCE = new None<>();

        private None() {
        }

        @Override
        public T getOrElse(T other) {
            return other;
        }

        @Override
        public T getOrElse(Supplier<? extends T> other) {
            return other.get();
        }

        @Override
        public boolean isEmpty() {
            return true;
        }


        @Override
        public <L> Either<L, T> toEither(Supplier<L> leftSupplier) {
            Objects.requireNonNull(leftSupplier, "leftSupplier must not be null");
            return Either.left(leftSupplier.get());
        }

        @Override
        public <U> Option<U> map(Function1<? super T, ? extends U> mapper) {
            Objects.requireNonNull(mapper, "mapper must not be null");
            return none();
        }

        @Override
        public <U> Option<U> flatMap(Function1<? super T, Option<U>> mapper) {
            Objects.requireNonNull(mapper, "mapper must not be null");
            return none();
        }

        @Override
        public Option<T> filter(Predicate<? super T> predicate) {
            Objects.requireNonNull(predicate, "predicate must not be null");
            return this;
        }

        @Override
        public <U> U match(Supplier<? extends U> noneCase, Function1<? super T, ? extends U> someCase) {
            Objects.requireNonNull(noneCase, "noneCase must not be null");
            return noneCase.get();
        }

        @Override
        public void forEach(Consumer<? super T> action) {
            Objects.requireNonNull(action, "action must not be null");
        }

        @Override
        public T get() {
            throw new NoSuchElementException("Cannot get value from None");
        }

        @Override
        public Option<T> orElse(Supplier<? extends Option<T>> alternative) {
            Objects.requireNonNull(alternative, "alternative must not be null");
            return alternative.get();
        }

        @Override
        public <X extends Throwable> T getOrElseThrow(Supplier<? extends X> exceptionSupplier) throws X {
            Objects.requireNonNull(exceptionSupplier, "exceptionSupplier must not be null");
            throw exceptionSupplier.get();
        }

        @Override
        public Stream<T> stream() {
            return Stream.empty();
        }

        @Override
        public Optional<T> toOptional() {
            return Optional.empty();
        }

        @Override
        public boolean equals(Object obj) {
            return this == obj || (obj != null && getClass() == obj.getClass());
        }

        @Override
        public int hashCode() {
            return 0;
        }

        @Override
        public String toString() {
            return "None";
        }
    }
}