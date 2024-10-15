package hydrafp.io.core.tco;

import hydrafp.io.core.adt.Either;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Implements a stepwise recursive computation built on top of TailRec.
 * Provides a simple API for defining recursive computations step by step.
 * @param <T> The type of the computation result.
 */
public class StepwiseRecursion<T> implements RecursiveComputation<T> {
    private final T initial;
    private final List<Function<T, Either<Throwable, T>>> steps;
    private final Predicate<T> isDone;

    private StepwiseRecursion(T initial, List<Function<T, Either<Throwable, T>>> steps, Predicate<T> isDone) {
        this.initial = initial;
        this.steps = steps;
        this.isDone = isDone;
    }

    /**
     * Creates a new StepwiseRecursion with the given initial value.
     * @param initial The initial value of the computation.
     * @param <T> The type of the computation result.
     * @return A StepwiseRecursionBuilder to continue building the computation.
     */
    public static <T> StepwiseRecursionBuilder<T> start(T initial) {
        return new StepwiseRecursionBuilder<>(initial);
    }

    @Override
    public Either<Throwable, T> compute() {
        return toTailRec().run();
    }

    private TailRec<T> toTailRec() {
        return new TailRec<T>() {
            private T current = initial;

            @Override
            public Either<Throwable, TailRec<T>> resume() {
                if (isDone.test(current)) {
                    return Either.right(TailRec.pure(current));
                }
                for (Function<T, Either<Throwable, T>> step : steps) {
                    Either<Throwable, T> result = step.apply(current);
                    if (result.isLeft()) {
                        return Either.left(result.getLeft());
                    }
                    current = result.getRight();
                    if (isDone.test(current)) {
                        return Either.right(TailRec.pure(current));
                    }
                }
                return Either.right(this);
            }
        };
    }

    /**
     * Builder class for creating StepwiseRecursion instances.
     * @param <T> The type of the computation result.
     */
    public static class StepwiseRecursionBuilder<T> {
        private final T initial;
        private final List<Function<T, Either<Throwable, T>>> steps = new ArrayList<>();
        private Predicate<T> isDone = t -> false;

        private StepwiseRecursionBuilder(T initial) {
            this.initial = initial;
        }

        /**
         * Adds a step to the computation.
         * @param step The step function to add.
         * @return This builder instance.
         */
        public StepwiseRecursionBuilder<T> step(Function<T, T> step) {
            steps.add(t -> Either.catching(() -> step.apply(t), Throwable::new));
            return this;
        }

        /**
         * Adds a step that may fail to the computation.
         * @param step The step function that may fail.
         * @return This builder instance.
         */
        public StepwiseRecursionBuilder<T> stepEither(Function<T, Either<Throwable, T>> step) {
            steps.add(step);
            return this;
        }

        /**
         * Sets the condition for when the computation is done.
         * @param isDone The predicate determining when the computation is complete.
         * @return This builder instance.
         */
        public StepwiseRecursionBuilder<T> until(Predicate<T> isDone) {
            this.isDone = isDone;
            return this;
        }

        /**
         * Builds the StepwiseRecursion instance.
         * @return A new StepwiseRecursion instance.
         */
        public StepwiseRecursion<T> build() {
            return new StepwiseRecursion<>(initial, steps, isDone);
        }
    }
}