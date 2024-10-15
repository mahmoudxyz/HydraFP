package hydrafp.io.core.adt;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public abstract class Try<T> {
    private Try() {
    }

    public static <T> Try<T> of(Supplier<? extends T> supplier) {
        Objects.requireNonNull(supplier, "supplier must not be null");
        try {
            return new Success<>(supplier.get());
        } catch (Throwable t) {
            return new Failure<>(t);
        }
    }

    public static <T> Try<T> success(T value) {
        return new Success<>(value);
    }

    public static <T> Try<T> failure(Throwable error) {
        return new Failure<>(Objects.requireNonNull(error, "error must not be null"));
    }

    public abstract boolean isSuccess();

    public abstract boolean isFailure();

    public abstract T get();

    public abstract Throwable getFailure();

    public abstract <U> Try<U> map(Function<? super T, ? extends U> mapper);

    public abstract <U> Try<U> flatMap(Function<? super T, ? extends Try<U>> mapper);

    public abstract Try<T> filter(Predicate<? super T> predicate);

    public abstract Try<T> recover(Function<? super Throwable, ? extends T> recoverer);

    public abstract Try<T> recoverWith(Function<? super Throwable, ? extends Try<T>> recoverer);

    public abstract T getOrElse(T defaultValue);

    public abstract Try<T> orElse(Supplier<? extends Try<T>> alternative);

    public abstract <U> U fold(Function<? super Throwable, ? extends U> failureMapper, Function<? super T, ? extends U> successMapper);

    public abstract Optional<T> toOptional();

    public abstract Try<T> onSuccess(Consumer<? super T> action);

    public abstract Try<T> onFailure(Consumer<? super Throwable> action);

    public abstract Option<T> toOption();

    public abstract <U> U transform(Function<? super T, ? extends U> successTransform, Function<? super Throwable, ? extends U> failureTransform);

    public abstract <L> Either<L, T> toEither(Function<Throwable, L> leftMapper);

    public abstract <X extends Throwable> T getOrElseThrow(Function<? super Throwable, X> exceptionFunction) throws X;

    public static <T> Try<T> fromEither(Either<Throwable, T> either) {
        Objects.requireNonNull(either, "either must not be null");
        return either.fold(Try::failure, Try::success);
    }

    public static <T> Try<T> fromOption(Option<T> option, Supplier<? extends Throwable> ifNone) {
        Objects.requireNonNull(option, "option must not be null");
        Objects.requireNonNull(ifNone, "ifNone must not be null");
        return option.map(Try::success)
                .getOrElse(() -> Try.failure(ifNone.get()));
    }

    public <U> Try<U> flatMapOption(Function<? super T, Option<U>> mapper) {
        Objects.requireNonNull(mapper, "mapper must not be null");
        return flatMap(value -> fromOption(mapper.apply(value), () -> new NoSuchElementException("Option is None")));
    }



    private static final class Success<T> extends Try<T> {
        private final T value;

        private Success(T value) {
            this.value = value;
        }

        @Override
        public boolean isSuccess() {
            return true;
        }

        @Override
        public boolean isFailure() {
            return false;
        }

        @Override
        public T get() {
            return value;
        }

        @Override
        public Throwable getFailure() {
            throw new UnsupportedOperationException("getFailure on Success");
        }

        @Override
        public <U> Try<U> map(Function<? super T, ? extends U> mapper) {
            Objects.requireNonNull(mapper, "mapper must not be null");
            return Try.of(() -> mapper.apply(value));
        }

        @Override
        public <U> Try<U> flatMap(Function<? super T, ? extends Try<U>> mapper) {
            Objects.requireNonNull(mapper, "mapper must not be null");
            try {
                return Objects.requireNonNull(mapper.apply(value), "mapper result must not be null");
            } catch (Throwable t) {
                return new Failure<>(t);
            }
        }

        @Override
        public Try<T> filter(Predicate<? super T> predicate) {
            Objects.requireNonNull(predicate, "predicate must not be null");
            return predicate.test(value) ? this : new Failure<>(new NoSuchElementException("Predicate does not hold for " + value));
        }

        @Override
        public Try<T> recover(Function<? super Throwable, ? extends T> recoverer) {
            return this;
        }

        @Override
        public Try<T> recoverWith(Function<? super Throwable, ? extends Try<T>> recoverer) {
            return this;
        }

        @Override
        public T getOrElse(T defaultValue) {
            return value;
        }

        @Override
        public Try<T> orElse(Supplier<? extends Try<T>> alternative) {
            return this;
        }

        @Override
        public <U> U fold(Function<? super Throwable, ? extends U> failureMapper, Function<? super T, ? extends U> successMapper) {
            Objects.requireNonNull(successMapper, "successMapper must not be null");
            return successMapper.apply(value);
        }

        @Override
        public Optional<T> toOptional() {
            return Optional.ofNullable(value);
        }

        public static <T> Try<T> fromOption(Option<T> option, Supplier<? extends Throwable> ifNone) {
            Objects.requireNonNull(option, "option must not be null");
            Objects.requireNonNull(ifNone, "ifNone must not be null");
            return option.map(Try::success)
                    .getOrElse(() -> Try.failure(ifNone.get()));
        }

        public <U> Try<U> flatMapOption(Function<? super T, Option<U>> mapper) {
            Objects.requireNonNull(mapper, "mapper must not be null");
            return flatMap(value -> fromOption(mapper.apply(value), () -> new NoSuchElementException("Option is None")));
        }

        @Override
        public Try<T> onSuccess(Consumer<? super T> action) {
            Objects.requireNonNull(action, "action must not be null");
            action.accept(value);
            return this;
        }

        @Override
        public Try<T> onFailure(Consumer<? super Throwable> action) {
            return this;
        }

        @Override
        public Option<T> toOption() {
            return Option.some(value);
        }

        @Override
        public <U> U transform(Function<? super T, ? extends U> successTransform, Function<? super Throwable, ? extends U> failureTransform) {
            Objects.requireNonNull(successTransform, "successTransform must not be null");
            return successTransform.apply(value);
        }

        @Override
        public <L> Either<L, T> toEither(Function<Throwable, L> leftMapper) {
            return Either.right(value);
        }

        @Override
        public <X extends Throwable> T getOrElseThrow(Function<? super Throwable, X> exceptionFunction) {
            return value;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            Success<?> success = (Success<?>) obj;
            return Objects.equals(value, success.value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(value);
        }

        @Override
        public String toString() {
            return "Success(" + value + ")";
        }
    }

    private static final class Failure<T> extends Try<T> {
        private final Throwable exception;

        private Failure(Throwable exception) {
            this.exception = exception;
        }

        @Override
        public boolean isSuccess() {
            return false;
        }

        @Override
        public boolean isFailure() {
            return true;
        }

        @Override
        public T get() {
            throw new RuntimeException("Cannot get value from Failure", exception);
        }

        @Override
        public Throwable getFailure() {
            return exception;
        }

        @Override
        public <U> Try<U> map(Function<? super T, ? extends U> mapper) {
            Objects.requireNonNull(mapper, "mapper must not be null");
            return (Try<U>) this;
        }

        @Override
        public <U> Try<U> flatMap(Function<? super T, ? extends Try<U>> mapper) {
            Objects.requireNonNull(mapper, "mapper must not be null");
            return (Try<U>) this;
        }

        @Override
        public Try<T> filter(Predicate<? super T> predicate) {
            Objects.requireNonNull(predicate, "predicate must not be null");
            return this;
        }

        @Override
        public Try<T> recover(Function<? super Throwable, ? extends T> recoverer) {
            Objects.requireNonNull(recoverer, "recoverer must not be null");
            return Try.of(() -> recoverer.apply(exception));
        }

        @Override
        public Try<T> recoverWith(Function<? super Throwable, ? extends Try<T>> recoverer) {
            Objects.requireNonNull(recoverer, "recoverer must not be null");
            try {
                return Objects.requireNonNull(recoverer.apply(exception), "recoverer result must not be null");
            } catch (Throwable t) {
                return new Failure<>(t);
            }
        }

        @Override
        public T getOrElse(T defaultValue) {
            return defaultValue;
        }

        @Override
        public Try<T> orElse(Supplier<? extends Try<T>> alternative) {
            Objects.requireNonNull(alternative, "alternative must not be null");
            return alternative.get();
        }

        @Override
        public <U> U fold(Function<? super Throwable, ? extends U> failureMapper, Function<? super T, ? extends U> successMapper) {
            Objects.requireNonNull(failureMapper, "failureMapper must not be null");
            return failureMapper.apply(exception);
        }

        @Override
        public Optional<T> toOptional() {
            return Optional.empty();
        }

        @Override
        public Try<T> onSuccess(Consumer<? super T> action) {
            Objects.requireNonNull(action, "action must not be null");
            return this;
        }

        @Override
        public Try<T> onFailure(Consumer<? super Throwable> action) {
            Objects.requireNonNull(action, "action must not be null");
            action.accept(exception);
            return this;
        }

        @Override
        public Option<T> toOption() {
            return Option.none();
        }

        @Override
        public <U> U transform(Function<? super T, ? extends U> successTransform, Function<? super Throwable, ? extends U> failureTransform) {
            Objects.requireNonNull(failureTransform, "failureTransform must not be null");
            return failureTransform.apply(exception);
        }

        @Override
        public <L> Either<L, T> toEither(Function<Throwable, L> leftMapper) {
            Objects.requireNonNull(leftMapper, "leftMapper must not be null");
            return Either.left(leftMapper.apply(exception));
        }


        @Override
        public <X extends Throwable> T getOrElseThrow(Function<? super Throwable, X> exceptionFunction) throws X {
            throw exceptionFunction.apply(exception);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            Failure<?> failure = (Failure<?>) obj;
            return Objects.equals(exception, failure.exception);
        }

        @Override
        public int hashCode() {
            return Objects.hash(exception);
        }

        @Override
        public String toString() {
            return "Failure(" + exception + ")";
        }
    }
}