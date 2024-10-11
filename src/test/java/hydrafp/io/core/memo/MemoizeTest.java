package hydrafp.io.core.memo;

import org.junit.jupiter.api.Test;

import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

class MemoizeTest {

    @Test
    void testMemoizeFunction() {
        int[] counter = {0};
        Function<Integer, Integer> expensiveOperation = x -> {
            counter[0]++;
            return x * 2;
        };

        Function<Integer, Integer> memoized = Memoize.memoize(expensiveOperation);

        assertEquals(4, memoized.apply(2));
        assertEquals(1, counter[0]);

        assertEquals(4, memoized.apply(2));
        assertEquals(1, counter[0]);

        assertEquals(6, memoized.apply(3));
        assertEquals(2, counter[0]);
    }

    @Test
    void testMemoizeBiFunction() {
        int[] counter = {0};
        Memoize.BiFunction<Integer, Integer, Integer> expensiveOperation = (x, y) -> {
            counter[0]++;
            return x + y;
        };

        Memoize.BiFunction<Integer, Integer, Integer> memoized = Memoize.memoize(expensiveOperation);

        assertEquals(5, memoized.apply(2, 3));
        assertEquals(1, counter[0]);

        assertEquals(5, memoized.apply(2, 3));
        assertEquals(1, counter[0]);

        assertEquals(7, memoized.apply(3, 4));
        assertEquals(2, counter[0]);
    }
}