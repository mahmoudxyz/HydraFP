
import hydrafp.io.core.adt.Option;
import hydrafp.io.pattern.Match;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PatternMatchingTest {

    @Test
    void testSimpleValueMatching() {
        Option<Integer> result = Match.of(5)
                .<Integer>when(1).then(10)
                .when(5).then(50)
                .when(10).then(100)
                .otherwise(0);

        assertEquals(50, result.getOrElse(-1));
    }

    @Test
    void testPredicateMatching() {
        Option<String> result = Match.of("hello")
                .<String>when(String::isEmpty).then("Empty")
                .when(s -> s.length() < 5).then("Short")
                .when(s -> s.length() >= 5).then("Long")
                .otherwise("Unknown");

        assertEquals("Long", result.getOrElse(""));
    }

    @Test
    void testTypeMatching() {
        Object value = "test";
        Option<String> result = Match.of(value)
                .whenType(Integer.class).then(i -> "Int: " + i)
                .whenType(String.class).then(s -> "String: " + s)
                .whenType(Boolean.class).then(b -> "Bool: " + b)
                .otherwise("Unknown type");

        assertEquals("String: test", result.getOrElse(""));
    }

    @Test
    void testNoMatchWithoutOtherwise() {
        Option<Integer> result = Match.of(100)
                .<Integer>when(1).then(10)
                .when(5).then(50)
                .result();

        assertTrue(result.isEmpty());
    }
    @Test
    void testNoMatchReturnsEmpty() {
        Option<String> result = Match.of(42)
                .<String>when(1).then("One")
                .when(2).then("Two")
                .when(3).then("Three")
                .result();

        assertTrue(result.isEmpty());
    }
    @Test
    void testNoMatchWithPredicateReturnsEmpty() {
        Option<String> result = Match.of("hello")
                .<String>when(String::isEmpty).then("Empty")
                .when(s -> s.length() > 10).then("Long")
                .result();

        assertTrue(result.isEmpty());
    }

    @Test
    void testNoMatchWithTypeMatchingReturnsEmpty() {
        Option<String> result = Match.of(42)
                .whenType(String.class).then(s -> "String: " + s)
                .whenType(Boolean.class).then(b -> "Boolean: " + b)
                .result();

        assertTrue(result.isEmpty());
    }


    @Test
    void testNullResult() {
        Option<String> result = Match.of("test")
                .<String>when("test").then((String) null)
                .otherwise("Not null");

        assertTrue(result.isEmpty());
    }
}