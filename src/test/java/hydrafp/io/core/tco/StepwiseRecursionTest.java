package hydrafp.io.core.tco;

import hydrafp.io.core.adt.Either;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class StepwiseRecursionTest {

    @Test
    @DisplayName("Compute factorial using StepwiseRecursion")
    void computeFactorial() {
        StepwiseRecursion<Integer> factorial = StepwiseRecursion.start(5)
                .step(n -> n - 1)
                .until(n -> n <= 1)
                .build();

        assertEquals(Either.right(1), factorial.compute());
    }

    @Test
    @DisplayName("Compute sum of numbers from 1 to n")
    void computeSum() {
        StepwiseRecursion<int[]> sumComputation = StepwiseRecursion.start(new int[]{10, 0})
                .step(arr -> new int[]{arr[0] - 1, arr[1] + arr[0]})
                .until(arr -> arr[0] == 0)
                .build();

        Either<Throwable, int[]> result = sumComputation.compute();
        assertTrue(result.isRight() && Arrays.equals(new int[]{0, 55}, result.getRight()));
    }

    @Test
    @DisplayName("Handle exception in step function")
    void handleExceptionInStep() {
        StepwiseRecursion<Integer> exceptionComputation = StepwiseRecursion.start(5)
                .step(n -> {
                    if (n == 3) throw new RuntimeException("Error at 3");
                    return n - 1;
                })
                .until(n -> n <= 0)
                .build();

        assertTrue(exceptionComputation.compute().isLeft());
    }

    @Test
    @DisplayName("Use stepEither for custom error handling")
    void useStepEither() {
        StepwiseRecursion<Integer> customErrorComputation = StepwiseRecursion.start(5)
                .stepEither(n -> n == 3 ? Either.left(new Exception("Custom error")) : Either.right(n - 1))
                .until(n -> n <= 0)
                .build();

        assertTrue(customErrorComputation.compute().isLeft());
    }

    @Test
    @DisplayName("Compute with no steps (only initial value)")
    void computeWithNoSteps() {
        StepwiseRecursion<Integer> noStepComputation = StepwiseRecursion.start(42)
                .until(n -> true)
                .build();

        assertEquals(Either.right(42), noStepComputation.compute());
    }

    @Test
    @DisplayName("Compute with long-running termination condition")
    void computeWithLongRunningCondition() {
        final int SAFETY_LIMIT = 1000000;
        AtomicInteger counter = new AtomicInteger(0);

        StepwiseRecursion<Integer> longRunningComputation = StepwiseRecursion.start(0)
                .step(n -> {
                    if (counter.incrementAndGet() >= SAFETY_LIMIT) {
                        throw new RuntimeException("Safety limit reached");
                    }
                    return n + 1;
                })
                .until(n -> false)
                .build();

        Either<Throwable, Integer> result = longRunningComputation.compute();

        assertTrue(result.isLeft());
        assertTrue(counter.get() == SAFETY_LIMIT);
    }

    @Test
    @DisplayName("Accumulate results in a list")
    void accumulateResultsInList() {
        StepwiseRecursion<ArrayList<Integer>> listAccumulation = StepwiseRecursion.start(new ArrayList<>(List.of(1)))
                .step(list -> {
                    ArrayList<Integer> newList = new ArrayList<>(list);
                    newList.add(list.get(list.size() - 1) + 1);
                    return newList;
                })
                .until(list -> list.size() >= 5)
                .build();

        assertEquals(Either.right(List.of(1, 2, 3, 4, 5)), listAccumulation.compute());
    }

    @Test
    @DisplayName("Compute with state transition")
    void computeWithStateTransition() {
        class State {
            final int value;
            final String status;
            State(int value, String status) {
                this.value = value;
                this.status = status;
            }

            @Override
            public boolean equals(Object o) {
                if (this == o) return true;
                if (o == null || getClass() != o.getClass()) return false;
                State state = (State) o;
                return value == state.value && Objects.equals(status, state.status);
            }

            @Override
            public int hashCode() {
                return Objects.hash(value, status);
            }

            @Override
            public String toString() {
                return "State{value=" + value + ", status='" + status + "'}";
            }
        }

        StepwiseRecursion<State> stateTransition = StepwiseRecursion.start(new State(0, "start"))
                .step(s -> new State(s.value + 1, (s.value + 1) % 2 == 0 ? "even" : "odd"))
                .until(s -> s.value >= 5)
                .build();

        assertEquals(Either.right(new State(5, "odd")), stateTransition.compute());
    }

    @Test
    @DisplayName("Terminate immediately with initial condition")
    void terminateImmediately() {
        StepwiseRecursion<Integer> immediateTermination = StepwiseRecursion.start(5)
                .step(n -> n + 1)
                .until(n -> n >= 5)
                .build();

        assertEquals(Either.right(5), immediateTermination.compute());
    }

    @Test
    @DisplayName("Compute with multiple steps before termination")
    void computeWithMultipleSteps() {
        StepwiseRecursion<Integer> multiStepComputation = StepwiseRecursion.start(0)
                .step(n -> n + 1)
                .step(n -> n * 2)
                .step(n -> n - 1)
                .until(n -> n > 10)
                .build();

        // The computation proceeds as follows:
        // 0 -> 1 -> 2 -> 1
        // 1 -> 2 -> 4 -> 3
        // 3 -> 4 -> 8 -> 7
        // 7 -> 8 -> 16 (stops here because 16 > 10)

        assertEquals(Either.right(16), multiStepComputation.compute());
    }

}