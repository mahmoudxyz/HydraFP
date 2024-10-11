package hydrafp.io.pattern;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WhenTest {

    @Test
    void testEqualityMatch() {
        String result = When.subject(2)
                .is(1, () -> "One")
                .is(2, () -> "Two")
                .is(3, () -> "Three")
                .eval();
        assertEquals("Two", result);
    }

    @Test
    void testPredicateMatch() {
        String result = When.subject(5)
                .is(x -> x < 0, () -> "Negative")
                .is(x -> x == 0, () -> "Zero")
                .is(x -> x > 0 && x <= 10, () -> "Between 1 and 10")
                .is(x -> x > 10, () -> "Greater than 10")
                .eval();
        assertEquals("Between 1 and 10", result);
    }

    @Test
    void testElseCase() {
        String result = When.subject(100)
                .is(1, () -> "One")
                .is(2, () -> "Two")
                .is(3, () -> "Three")
                .elseIs(() -> "Something else")
                .eval();
        assertEquals("Something else", result);
    }

    @Test
    void testNoMatchNoElse() {
        assertThrows(IllegalStateException.class, () -> {
            When.subject(4)
                    .is(1, () -> "One")
                    .is(2, () -> "Two")
                    .is(3, () -> "Three")
                    .eval();
        });
    }

    @Test
    void testMultipleMatches() {
        String result = When.subject(3)
                .is(x -> x > 0, () -> "Positive")
                .is(3, () -> "Three")
                .is(x -> x % 2 != 0, () -> "Odd")
                .eval();
        assertEquals("Positive", result);
    }

    @Test
    void testDifferentTypes() {
        Object result = When.subject("test")
                .is(1, () -> 1)
                .is("test", () -> true)
                .is(2.0, () -> 2.0)
                .eval();
        assertEquals(true, result);
    }

    @Test
    void testDifferentReturnTypes() {
        Integer intResult = When.subject(5)
                .is(1, () -> "One")
                .is(5, () -> 5)
                .is(10, () -> 10.0)
                .eval();
        assertEquals(5, intResult);

        String stringResult = When.subject(1)
                .is(1, () -> "One")
                .is(5, () -> 5)
                .is(10, () -> 10.0)
                .eval();
        assertEquals("One", stringResult);

        Double doubleResult = When.subject(10)
                .is(1, () -> "One")
                .is(5, () -> 5)
                .is(10, () -> 10.0)
                .eval();
        assertEquals(10.0, doubleResult);
    }

}