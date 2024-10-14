package hydrafp.io.core.tco;

import hydrafp.io.core.adt.Pair;
import hydrafp.io.core.adt.Triple;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

class RecTest {

    @Nested
    class FactorialTest {
        private final Function<Pair<BigInteger, BigInteger>, Pair<BigInteger, BigInteger>> factorial =
                Rec.<Pair<BigInteger, BigInteger>>when(p -> p.first().equals(BigInteger.ZERO))
                        .step(p -> Pair.of(p.first().subtract(BigInteger.ONE), p.second().multiply(p.first())))
                        .build();

        @ParameterizedTest
        @CsvSource({"0,1", "1,1", "2,2", "3,6", "4,24", "5,120", "10,3628800"})
        void testFactorialForSmallNumbers(int n, long expected) {
            BigInteger result = factorial.apply(Pair.of(BigInteger.valueOf(n), BigInteger.ONE)).second();
            assertEquals(BigInteger.valueOf(expected), result);
        }

        @Test
        void testFactorialForLargeNumber() {
            BigInteger result = factorial.apply(Pair.of(BigInteger.valueOf(1000), BigInteger.ONE)).second();
            assertTrue(result.toString().length() > 2500);
            assertTrue(result.toString().startsWith("402387260077093773543702"));
        }
    }

    @Nested
    class FibonacciTest {
        private final Function<Triple<BigInteger, BigInteger, BigInteger>, Triple<BigInteger, BigInteger, BigInteger>> fibonacci =
                Rec.<Triple<BigInteger, BigInteger, BigInteger>>when(t -> t.first().equals(BigInteger.ZERO))
                        .step(t -> Triple.of(
                                t.first().subtract(BigInteger.ONE),
                                t.third(),
                                t.second().add(t.third())
                        ))
                        .build();

        @ParameterizedTest
        @CsvSource({"0,0", "1,1", "2,1", "3,2", "4,3", "5,5", "6,8", "7,13", "8,21", "9,34", "10,55"})
        void testFibonacciForSmallNumbers(int n, long expected) {
            BigInteger result = fibonacci.apply(Triple.of(BigInteger.valueOf(n), BigInteger.ZERO, BigInteger.ONE)).second();
            assertEquals(BigInteger.valueOf(expected), result);
        }

        @Test
        void testFibonacciForLargeNumber() {
            BigInteger result = fibonacci.apply(Triple.of(BigInteger.valueOf(1000), BigInteger.ZERO, BigInteger.ONE)).second();
            assertTrue(result.toString().length() > 200);
            assertTrue(result.toString().startsWith("43466557686937456435688527"));
        }
    }

    @Nested
    class CollatzConjectureTest {
        private final Function<Pair<Long, List<Long>>, Pair<Long, List<Long>>> collatz =
                Rec.<Pair<Long, List<Long>>>when(p -> p.first() == 1)
                        .step(p -> {
                            long next = (p.first() % 2 == 0) ? p.first() / 2 : 3 * p.first() + 1;
                            List<Long> newSequence = new ArrayList<>(p.second());
                            newSequence.add(next);
                            return Pair.of(next, newSequence);
                        })
                        .build();

        @Test
        void testCollatzForSmallNumber() {
            List<Long> sequence = collatz.apply(Pair.of(6L, new ArrayList<>(List.of(6L)))).second();
            assertEquals(List.of(6L, 3L, 10L, 5L, 16L, 8L, 4L, 2L, 1L), sequence);
        }

        @Test
        void testCollatzForLargeNumber() {
            List<Long> sequence = collatz.apply(Pair.of(27L, new ArrayList<>(List.of(27L)))).second();
            assertTrue(sequence.size() > 100);
            assertEquals(1L, sequence.get(sequence.size() - 1));
        }
    }

    @Test
    void testRecWithNoSteps() {
        var identity = Rec.<Integer>when(x -> true).build();
        assertEquals(5, identity.apply(5));
    }

    @Test
    void testRecWithMultipleSteps() {
        var multiStep = Rec.<Integer>when(x -> x >= 10)
                .step(x -> x + 1)
                .step(x -> x * 2)
                .build();
        assertEquals(12, multiStep.apply(5));
    }
}