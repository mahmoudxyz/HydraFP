package hydrafp.io.core.lazy;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class LazyTest {

    @Test
    void testLazyEvaluation() {
        int[] counter = {0};
        Lazy<Integer> lazy = Lazy.of(() -> {
            counter[0]++;
            return 42;
        });

        assertFalse(lazy.isEvaluated());
        assertEquals(0, counter[0]);
    }

    @Test
    void testLazyGet() {
        int[] counter = {0};
        Lazy<Integer> lazy = Lazy.of(() -> {
            counter[0]++;
            return 42;
        });

        assertEquals(42, lazy.get());
        assertEquals(1, counter[0]);
    }

    @Test
    void testLazyGetMultipleTimes() {
        int[] counter = {0};
        Lazy<Integer> lazy = Lazy.of(() -> {
            counter[0]++;
            return 42;
        });

        assertEquals(42, lazy.get());
        assertEquals(42, lazy.get());
        assertEquals(42, lazy.get());
        assertEquals(1, counter[0]);
    }

    @Test
    void testLazyIsEvaluated() {
        Lazy<Integer> lazy = Lazy.of(() -> 42);

        assertFalse(lazy.isEvaluated());
        lazy.get();
        assertTrue(lazy.isEvaluated());
    }

    @Test
    void testLazyMap() {
        Lazy<Integer> lazy = Lazy.of(() -> 21);
        Lazy<Integer> mapped = lazy.map(x -> x * 2);

        assertFalse(mapped.isEvaluated());
        assertEquals(42, mapped.get());
        assertTrue(mapped.isEvaluated());
    }

    @Test
    void testLazyFlatMap() {
        Lazy<Integer> lazy = Lazy.of(() -> 21);
        Lazy<Integer> flatMapped = lazy.flatMap(x -> Lazy.of(() -> x * 2));

        assertFalse(flatMapped.isEvaluated());
        assertEquals(42, flatMapped.get());
        assertTrue(flatMapped.isEvaluated());
    }
}