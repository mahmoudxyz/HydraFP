package core.adt;

import hydrafp.io.core.adt.Either;
import hydrafp.io.pattern.Match;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class EitherAndMatchTest {

    @Test
    void testEitherCreation() {
        Either<String, Integer> left = Either.left("error");
        Either<String, Integer> right = Either.right(42);

        assertTrue(left.isLeft());
        assertFalse(left.isRight());
        assertTrue(right.isRight());
        assertFalse(right.isLeft());
    }

    @Test
    void testEitherFold() {
        Either<String, Integer> left = Either.left("error");
        Either<String, Integer> right = Either.right(42);

        String leftResult = left.fold(l -> "Left: " + l, r -> "Right: " + r);
        String rightResult = right.fold(l -> "Left: " + l, r -> "Right: " + r);

        assertEquals("Left: error", leftResult);
        assertEquals("Right: 42", rightResult);
    }

    @Test
    void testEitherMap() {
        Either<String, Integer> right = Either.right(42);
        Either<String, String> mapped = right.map(i -> "Mapped: " + i);

        assertTrue(mapped.isRight());
        assertEquals("Mapped: 42", mapped.getRight());
    }

    @Test
    void testEitherFlatMap() {
        Either<String, Integer> right = Either.right(42);
        Either<String, String> flatMapped = right.flatMap(i -> Either.right("FlatMapped: " + i));

        assertTrue(flatMapped.isRight());
        assertEquals("FlatMapped: 42", flatMapped.getRight());
    }

    @Test
    void testEitherGetRightOrElse() {
        Either<String, Integer> left = Either.left("error");
        Either<String, Integer> right = Either.right(42);

        assertEquals(0, left.getRightOrElse(0));
        assertEquals(42, right.getRightOrElse(0));
    }

    @Test
    void testEitherGetLeftOrElse() {
        Either<String, Integer> left = Either.left("error");
        Either<String, Integer> right = Either.right(42);

        assertEquals("error", left.getLeftOrElse("default"));
        assertEquals("default", right.getLeftOrElse("default"));
    }



    @Test
    void testMatchWithEither() {
        Either<String, Integer> left = Either.left("error");
        Either<String, Integer> right = Either.right(42);

        String leftResult = Match.of(left)
                .<String, Integer>whenLeft(String.class).then(s -> "Left: " + s)
                .<String, Integer>whenRight(Integer.class).then(i -> "Right: " + i)
                .otherwise("Unknown")
                .getOrElse("Default");

        String rightResult = Match.of(right)
                .<String, Integer>whenLeft(String.class).then(s -> "Left: " + s)
                .<String, Integer>whenRight(Integer.class).then(i -> "Right: " + i)
                .otherwise("Unknown")
                .getOrElse("Default");

        assertEquals("Left: error", leftResult);
        assertEquals("Right: 42", rightResult);
    }

    @Test
    void testMatchWithEitherAndTypeMatching() {
        Either<Exception, Object> stringEither = Either.right("Hello");
        Either<Exception, Object> intEither = Either.right(42);
        Either<Exception, Object> errorEither = Either.left(new RuntimeException("Error"));

        String stringResult = Match.of(stringEither)
                .whenLeft(Exception.class).then(e -> "Error: " + e.getMessage())
                .<Exception, Object>whenRight(Object.class).then(o -> {
                    if (o instanceof String) return "String: " + o;
                    if (o instanceof Integer) return "Int: " + o;
                    return "Unknown type";
                })
                .otherwise("Unknown")
                .getOrElse("Default");

        String intResult = Match.of(intEither)
                .whenLeft(Exception.class).then(e -> "Error: " + e.getMessage())
                .<Exception, Object>whenRight(Object.class).then(o -> {
                    if (o instanceof String) return "String: " + o;
                    if (o instanceof Integer) return "Int: " + o;
                    return "Unknown type";
                })
                .otherwise("Unknown")
                .getOrElse("Default");

        String errorResult = Match.of(errorEither)
                .whenLeft(Exception.class).then(e -> "Error: " + e.getMessage())
                .<Exception, Object>whenRight(Object.class).then(o -> {
                    if (o instanceof String) return "String: " + o;
                    if (o instanceof Integer) return "Int: " + o;
                    return "Unknown type";
                })
                .otherwise("Unknown")
                .getOrElse("Default");

        assertEquals("String: Hello", stringResult);
        assertEquals("Int: 42", intResult);
        assertEquals("Error: Error", errorResult);
    }

    @Test
    void testMatchWithEitherAndPredicates() {
        Either<String, Integer> positiveEither = Either.right(42);
        Either<String, Integer> negativeEither = Either.right(-42);
        Either<String, Integer> errorEither = Either.left("Invalid number");

        String positiveResult = Match.of(positiveEither)
                .<String, Integer>whenLeft(String.class).then(s -> "Error: " + s)
                .<String, Integer>whenRight(Integer.class).then(i -> i > 0 ? "Positive" : "Non-positive")
                .otherwise("Unknown")
                .getOrElse("Default");

        String negativeResult = Match.of(negativeEither)
                .<String, Integer>whenLeft(String.class).then(s -> "Error: " + s)
                .<String, Integer>whenRight(Integer.class).then(i -> i > 0 ? "Positive" : "Non-positive")
                .otherwise("Unknown")
                .getOrElse("Default");

        String errorResult = Match.of(errorEither)
                .<String, Integer>whenLeft(String.class).then(s -> "Error: " + s)
                .<String, Integer>whenRight(Integer.class).then(i -> i > 0 ? "Positive" : "Non-positive")
                .otherwise("Unknown")
                .getOrElse("Default");

        assertEquals("Positive", positiveResult);
        assertEquals("Non-positive", negativeResult);
        assertEquals("Error: Invalid number", errorResult);
    }



}