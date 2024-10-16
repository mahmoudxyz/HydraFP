package hydrafp.io.core.tco;

import hydrafp.io.core.adt.Either;
import hydrafp.io.core.adt.Option;
import hydrafp.io.core.adt.Try;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;

/**
 * Represents a recursive computation that can be evaluated lazily and safely.
 * @param <T> The type of the computation result.
 */
public interface RecursiveComputation<T> {
    /**
     * Computes the result of this computation.
     * @return An "Either" containing either the computed result or an error.
     */
    Either<Throwable, T> compute();

    /**
     * Returns a memoized version of this computation.
     * @return A new RecursiveComputation that caches its result after the first computation.
     */
    default RecursiveComputation<T> memoize() {
        return new MemoizedComputation<>(this);
    }

    /**
     * Returns a new computation with a timeout using a default executor.
     * @param timeout The duration of the timeout.
     * @param unit The time unit of the timeout duration.
     * @return A new RecursiveComputation that will timeout if it exceeds the specified duration.
     */
    default RecursiveComputation<T> timeout(long timeout, TimeUnit unit) {
        return timeout(timeout, unit, CompletableFuture.delayedExecutor(0, TimeUnit.SECONDS));
    }

    /**
     * Returns a new computation with a timeout using the specified executor.
     * @param timeout The duration of the timeout.
     * @param unit The time unit of the timeout duration.
     * @param executor The executor to use for running the computation.
     * @return A new RecursiveComputation that will timeout if it exceeds the specified duration.
     */
    default RecursiveComputation<T> timeout(long timeout, TimeUnit unit, Executor executor) {
        return () -> {
            CompletableFuture<Either<Throwable, T>> future = CompletableFuture.supplyAsync(this::compute, executor);

            try {
                return future.get(timeout, unit);
            } catch (TimeoutException e) {
                future.cancel(true);
                return Either.left(new TimeoutException("Computation timed out after " + timeout + " " + unit.name().toLowerCase()));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return Either.left(e);
            } catch (Exception e) {
                return Either.left(e);
            }
        };
    }

    /**
     * Maps the result of this computation using the provided function.
     * @param f The function to apply to the result.
     * @param <R> The type of the mapped result.
     * @return A new RecursiveComputation with the mapped result.
     */
    default <R> RecursiveComputation<R> map(Function<? super T, ? extends R> f) {
        return () -> compute().map(f);
    }

    /**
     * Flat maps the result of this computation using the provided function.
     * @param f The function to apply to the result, returning a new RecursiveComputation.
     * @param <R> The type of the flat mapped result.
     * @return A new RecursiveComputation with the flat mapped result.
     */
    default <R> RecursiveComputation<R> flatMap(Function<? super T, ? extends RecursiveComputation<R>> f) {
        return () -> compute().flatMap(t -> f.apply(t).compute());
    }

    /**
     * Creates a RecursiveComputation that always succeeds with the given value.
     * @param value The value to return.
     * @param <T> The type of the value.
     * @return A new RecursiveComputation that always succeeds with the given value.
     */
    static <T> RecursiveComputation<T> pure(T value) {
        return () -> Either.right(value);
    }

    /**
     * Creates a RecursiveComputation that always fails with the given error.
     * @param error The error to return.
     * @param <T> The type of the computation result.
     * @return A new RecursiveComputation that always fails with the given error.
     */
    static <T> RecursiveComputation<T> raiseError(Throwable error) {
        return () -> Either.left(error);
    }

    /**
     * Converts this computation to an Option.
     * @return An Option containing the result of the computation, or None if it failed.
     */
    default Option<T> toOption() {
        return compute().fold(
                error -> Option.none(),
                Option::some
        );
    }

    /**
     * Converts this computation to a Try.
     * @return A Try containing the result of the computation, or the error if it failed.
     */
    default Try<T> toTry() {
        return compute().fold(
                Try::failure,
                Try::success
        );
    }
}

/**
 * A memoized version of a RecursiveComputation that caches its result after the first computation.
 * @param <T> The type of the computation result.
 */
class MemoizedComputation<T> implements RecursiveComputation<T> {
    private final RecursiveComputation<T> computation;
    private volatile Either<Throwable, T> memoizedResult;

    MemoizedComputation(RecursiveComputation<T> computation) {
        this.computation = computation;
    }

    @Override
    public Either<Throwable, T> compute() {
        if (memoizedResult == null) {
            synchronized (this) {
                if (memoizedResult == null) {
                    memoizedResult = computation.compute();
                }
            }
        }
        return memoizedResult;
    }
}