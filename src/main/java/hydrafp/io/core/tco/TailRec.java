package hydrafp.io.core.tco;

import hydrafp.io.core.adt.Either;
import hydrafp.io.core.adt.Option;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Represents a tail-recursive computation that can be evaluated lazily and safely.
 * This interface extends RecursiveComputation, specializing in tail-recursive algorithms.
 *
 * @param <A> The type of the computation result.
 */
@FunctionalInterface
public interface TailRec<A> extends RecursiveComputation<A> {

    /**
     * Performs one step of the tail-recursive computation.
     *
     * @return Either the next step of the computation or the final result.
     */
    Either<Throwable, TailRec<A>> resume();

    /**
     * Computes the result of this tail-recursive computation.
     *
     * @return An Either containing either the computed result or an error.
     */
    @Override
    default Either<Throwable, A> compute() {
        return run();
    }

    /**
     * Runs the tail-recursive computation to completion.
     *
     * @return The final result of the computation.
     */
    default Either<Throwable, A> run() {
        TailRec<A> current = this;
        while (true) {
            Either<Throwable, TailRec<A>> resumed = current.resume();
            if (resumed.isLeft()) {
                return Either.left(resumed.getLeft());
            }
            TailRec<A> next = resumed.getRight();
            if (next instanceof Done) {
                return Either.right(((Done<A>) next).value);
            }
            current = next;
        }
    }

    /**
     * Converts the result of this computation to an Option.
     *
     * @return An Option containing the result of the computation, or None if it failed.
     */
    default Option<A> runOption() {
        return run().fold(
                error -> Option.none(),
                Option::some
        );
    }

    /**
     * Maps the result of this computation using the provided function.
     *
     * @param f   The function to apply to the result.
     * @param <B> The type of the mapped result.
     * @return A new TailRec with the mapped result.
     */
    @Override
    default <B> TailRec<B> map(Function<? super A, ? extends B> f) {
        Objects.requireNonNull(f, "Mapping function must not be null");
        return flatMap(a -> pure(f.apply(a)));
    }

    /**
     * Flat maps the result of this computation using the provided function.
     *
     * @param f   The function to apply to the result, returning a new TailRec.
     * @param <B> The type of the flat mapped result.
     * @return A new TailRec with the flat mapped result.
     */
    @Override
    default <B> TailRec<B> flatMap(Function<? super A, ? extends RecursiveComputation<B>> f) {
        Objects.requireNonNull(f, "Flat mapping function must not be null");
        return defer(() -> run().flatMap(a -> {
            RecursiveComputation<B> computation = f.apply(a);
            if (computation instanceof TailRec) {
                return Either.right((TailRec<B>) computation);
            } else {
                return computation.compute().map(TailRec::pure);
            }
        }));
    }
    /**
     * Creates a TailRec that always succeeds with the given value.
     *
     * @param value The value to return.
     * @param <A>   The type of the value.
     * @return A new TailRec that always succeeds with the given value.
     */
    static <A> TailRec<A> pure(A value) {
        return new Done<>(value);
    }

    /**
     * Defers the execution of a TailRec computation.
     *
     * @param thunk A supplier that returns the next step of the computation.
     * @param <A>   The type of the computation result.
     * @return A new TailRec representing the deferred computation.
     */
    static <A> TailRec<A> defer(Supplier<Either<Throwable, TailRec<A>>> thunk) {
        Objects.requireNonNull(thunk, "Deferred computation must not be null");
        return new More<>(thunk);
    }

    /**
     * Suspends a computation that might throw an exception into a TailRec.
     *
     * @param thunk The computation to suspend.
     * @param <A>   The type of the computation result.
     * @return A new TailRec representing the suspended computation.
     */
    static <A> TailRec<A> suspend(Supplier<A> thunk) {
        Objects.requireNonNull(thunk, "Suspended computation must not be null");
        return defer(() -> {
            try {
                return Either.right(pure(thunk.get()));
            } catch (Throwable t) {
                return Either.left(t);
            }
        });
    }

    /**
     * Converts a function into a tail-recursive function.
     *
     * @param f   The function to convert.
     * @param <A> The input type of the function.
     * @param <B> The output type of the function.
     * @return A new function that returns a TailRec.
     */
    static <A, B> Function<A, TailRec<B>> tailRec(Function<A, TailRec<B>> f) {
        Objects.requireNonNull(f, "Function must not be null");
        return a -> new More<>(() -> Either.right(f.apply(a)));
    }

    /**
     * Creates a TailRec that always fails with the given error.
     *
     * @param error The error to return.
     * @param <A>   The type of the computation result.
     * @return A new TailRec that always fails with the given error.
     */
    static <A> TailRec<A> raiseError(Throwable error) {
        Objects.requireNonNull(error, "Error must not be null");
        return new Error<>(error);
    }

    /**
     * Handles errors in this TailRec computation.
     *
     * @param handler A function that handles errors and returns a new TailRec.
     * @return A new TailRec with error handling applied.
     */
    default TailRec<A> handleErrorWith(Function<Throwable, TailRec<A>> handler) {
        Objects.requireNonNull(handler, "Error handler must not be null");
        return defer(() -> run().fold(t -> Either.right(handler.apply(t)), a -> Either.right(pure(a))));
    }

    /**
     * Represents a completed TailRec computation.
     *
     * @param <A> The type of the computation result.
     */
    final class Done<A> implements TailRec<A> {
        private final A value;

        private Done(A value) {
            this.value = value;
        }

        @Override
        public Either<Throwable, TailRec<A>> resume() {
            return Either.right(this);
        }
    }

    /**
     * Represents a TailRec computation that has more steps to perform.
     *
     * @param <A> The type of the computation result.
     */
    final class More<A> implements TailRec<A> {
        private final Supplier<Either<Throwable, TailRec<A>>> next;

        private More(Supplier<Either<Throwable, TailRec<A>>> next) {
            this.next = next;
        }

        @Override
        public Either<Throwable, TailRec<A>> resume() {
            return next.get();
        }
    }

    /**
     * Represents a failed TailRec computation.
     *
     * @param <A> The type of the computation result.
     */
    final class Error<A> implements TailRec<A> {
        private final Throwable error;

        private Error(Throwable error) {
            this.error = error;
        }

        @Override
        public Either<Throwable, TailRec<A>> resume() {
            return Either.left(error);
        }
    }
}