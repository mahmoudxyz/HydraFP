package hydrafp.io.core.adt;

import org.junit.jupiter.api.Test;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicInteger;
import static org.junit.jupiter.api.Assertions.*;

class EitherTest {

    @Test
    void left_createsLeftInstance() {
        Either<String, Integer> left = Either.left("Error");
        assertTrue(left.isLeft());
    }

    @Test
    void right_createsRightInstance() {
        Either<String, Integer> right = Either.right(42);
        assertTrue(right.isRight());
    }

    @Test
    void left_throwsNullPointerExceptionForNullValue() {
        assertThrows(NullPointerException.class, () -> Either.left(null));
    }

    @Test
    void right_throwsNullPointerExceptionForNullValue() {
        assertThrows(NullPointerException.class, () -> Either.right(null));
    }

    @Test
    void fold_appliesLeftFunctionForLeft() {
        Either<String, Integer> left = Either.left("Error");
        String result = left.fold(l -> l + "!", r -> r.toString());
        assertEquals("Error!", result);
    }

    @Test
    void fold_appliesRightFunctionForRight() {
        Either<String, Integer> right = Either.right(42);
        String result = right.fold(l -> l, r -> r.toString());
        assertEquals("42", result);
    }

    @Test
    void map_appliesFunctionForRight() {
        Either<String, Integer> right = Either.right(21);
        Either<String, Integer> result = right.map(r -> r * 2);
        assertEquals(Integer.valueOf(42), result.getRight());
    }

    @Test
    void map_doesNotApplyFunctionForLeft() {
        Either<String, Integer> left = Either.left("Error");
        Either<String, Integer> result = left.map(r -> r * 2);
        assertTrue(result.isLeft());
    }

    @Test
    void flatMap_appliesFunctionForRight() {
        Either<String, Integer> right = Either.right(21);
        Either<String, Integer> result = right.flatMap(r -> Either.right(r * 2));
        assertEquals(Integer.valueOf(42), result.getRight());
    }

    @Test
    void flatMap_doesNotApplyFunctionForLeft() {
        Either<String, Integer> left = Either.left("Error");
        Either<String, Integer> result = left.flatMap(r -> Either.right(r * 2));
        assertTrue(result.isLeft());
    }

    @Test
    void mapLeft_appliesFunctionForLeft() {
        Either<String, Integer> left = Either.left("error");
        Either<String, Integer> result = left.mapLeft(String::toUpperCase);
        assertEquals("ERROR", result.getLeft());
    }

    @Test
    void mapLeft_doesNotApplyFunctionForRight() {
        Either<String, Integer> right = Either.right(42);
        Either<String, Integer> result = right.mapLeft(String::toUpperCase);
        assertTrue(result.isRight());
    }

    @Test
    void flatMapLeft_appliesFunctionForLeft() {
        Either<String, Integer> left = Either.left("error");
        Either<String, Integer> result = left.flatMapLeft(l -> Either.left(l.toUpperCase()));
        assertEquals("ERROR", result.getLeft());
    }

    @Test
    void flatMapLeft_doesNotApplyFunctionForRight() {
        Either<String, Integer> right = Either.right(42);
        Either<String, Integer> result = right.flatMapLeft(l -> Either.left(l.toUpperCase()));
        assertTrue(result.isRight());
    }

    @Test
    void filter_returnsRightWhenPredicateIsTrue() {
        Either<String, Integer> right = Either.right(42);
        Either<String, Integer> result = right.filter(r -> r > 20, () -> "Too small");
        assertTrue(result.isRight());
    }

    @Test
    void filter_returnsLeftWhenPredicateIsFalse() {
        Either<String, Integer> right = Either.right(10);
        Either<String, Integer> result = right.filter(r -> r > 20, () -> "Too small");
        assertEquals("Too small", result.getLeft());
    }

    @Test
    void filterOrElse_returnsRightWhenPredicateIsTrue() {
        Either<String, Integer> right = Either.right(42);
        Either<String, Integer> result = right.filterOrElse(r -> r > 20, r -> "Value: " + r);
        assertTrue(result.isRight());
    }

    @Test
    void filterOrElse_returnsLeftWhenPredicateIsFalse() {
        Either<String, Integer> right = Either.right(10);
        Either<String, Integer> result = right.filterOrElse(r -> r > 20, r -> "Value: " + r);
        assertEquals("Value: 10", result.getLeft());
    }

    @Test
    void isLeft_returnsTrueForLeft() {
        Either<String, Integer> left = Either.left("Error");
        assertTrue(left.isLeft());
    }

    @Test
    void isLeft_returnsFalseForRight() {
        Either<String, Integer> right = Either.right(42);
        assertFalse(right.isLeft());
    }

    @Test
    void isRight_returnsTrueForRight() {
        Either<String, Integer> right = Either.right(42);
        assertTrue(right.isRight());
    }

    @Test
    void isRight_returnsFalseForLeft() {
        Either<String, Integer> left = Either.left("Error");
        assertFalse(left.isRight());
    }

    @Test
    void getLeft_returnsValueForLeft() {
        Either<String, Integer> left = Either.left("Error");
        assertEquals("Error", left.getLeft());
    }

    @Test
    void getLeft_throwsNoSuchElementExceptionForRight() {
        Either<String, Integer> right = Either.right(42);
        assertThrows(NoSuchElementException.class, right::getLeft);
    }

    @Test
    void getRight_returnsValueForRight() {
        Either<String, Integer> right = Either.right(42);
        assertEquals(Integer.valueOf(42), right.getRight());
    }

    @Test
    void getRight_throwsNoSuchElementExceptionForLeft() {
        Either<String, Integer> left = Either.left("Error");
        assertThrows(NoSuchElementException.class, left::getRight);
    }

    @Test
    void toLeftOptional_returnsEmptyForRight() {
        Either<String, Integer> right = Either.right(42);
        assertTrue(right.toLeftOptional().isEmpty());
    }

    @Test
    void toLeftOptional_returnsValueForLeft() {
        Either<String, Integer> left = Either.left("Error");
        assertEquals("Error", left.toLeftOptional().get());
    }

    @Test
    void toRightOptional_returnsEmptyForLeft() {
        Either<String, Integer> left = Either.left("Error");
        assertTrue(left.toRightOptional().isEmpty());
    }

    @Test
    void toRightOptional_returnsValueForRight() {
        Either<String, Integer> right = Either.right(42);
        assertEquals(Integer.valueOf(42), right.toRightOptional().get());
    }

    @Test
    void swap_convertsLeftToRight() {
        Either<String, Integer> left = Either.left("Error");
        Either<Integer, String> swapped = left.swap();
        assertTrue(swapped.isRight());
        assertEquals("Error", swapped.getRight());
    }

    @Test
    void swap_convertsRightToLeft() {
        Either<String, Integer> right = Either.right(42);
        Either<Integer, String> swapped = right.swap();
        assertTrue(swapped.isLeft());
        assertEquals(Integer.valueOf(42), swapped.getLeft());
    }

    @Test
    void orElse_returnsOriginalForRight() {
        Either<String, Integer> right = Either.right(42);
        Either<String, Integer> result = right.orElse(() -> Either.right(0));
        assertTrue(result.isRight());
        assertEquals(Integer.valueOf(42), result.getRight());
    }

    @Test
    void orElse_returnsAlternativeLeftForLeft() {
        Either<String, Integer> left = Either.left("Error");
        Either<String, Integer> result = left.orElse(() -> Either.left("Alternative Error"));
        assertTrue(result.isLeft());
        assertEquals("Alternative Error", result.getLeft());
    }

    @Test
    void peek_executesActionForRight() {
        AtomicInteger counter = new AtomicInteger(0);
        Either<String, Integer> right = Either.right(42);
        Either<String, Integer> result = right.peek(r -> counter.incrementAndGet());
        assertEquals(1, counter.get());
        assertTrue(result.isRight());
    }

    @Test
    void orElse_throwsNullPointerExceptionForNullSupplier() {
        Either<String, Integer> left = Either.left("Error");
        assertThrows(NullPointerException.class, () -> left.orElse(null));
    }


    @Test
    void peek_doesNotExecuteActionForLeft() {
        AtomicInteger counter = new AtomicInteger(0);
        Either<String, Integer> left = Either.left("Error");
        Either<String, Integer> result = left.peek(r -> counter.incrementAndGet());
        assertEquals(0, counter.get());
        assertTrue(result.isLeft());
    }

    @Test
    void peekLeft_executesActionForLeft() {
        AtomicInteger counter = new AtomicInteger(0);
        Either<String, Integer> left = Either.left("Error");
        Either<String, Integer> result = left.peekLeft(l -> counter.incrementAndGet());
        assertEquals(1, counter.get());
        assertTrue(result.isLeft());
    }

    @Test
    void peekLeft_doesNotExecuteActionForRight() {
        AtomicInteger counter = new AtomicInteger(0);
        Either<String, Integer> right = Either.right(42);
        Either<String, Integer> result = right.peekLeft(l -> counter.incrementAndGet());
        assertEquals(0, counter.get());
        assertTrue(result.isRight());
    }

    // Tests for integration with Option
    @Test
    void toOption_returnsSomeForRight() {
        Either<String, Integer> right = Either.right(42);
        Option<Integer> result = right.toOption();
        assertTrue(result.isDefined());
        assertEquals(Integer.valueOf(42), result.get());
    }

    @Test
    void toOption_returnsNoneForLeft() {
        Either<String, Integer> left = Either.left("Error");
        Option<Integer> result = left.toOption();
        assertTrue(result.isEmpty());
    }

    @Test
    void fromOption_createsRightForSome() {
        Option<Integer> some = Option.some(42);
        Either<String, Integer> result = Either.fromOption(some, () -> "Error");
        assertTrue(result.isRight());
        assertEquals(Integer.valueOf(42), result.getRight());
    }

    @Test
    void fromOption_createsLeftForNone() {
        Option<Integer> none = Option.none();
        Either<String, Integer> result = Either.fromOption(none, () -> "Error");
        assertTrue(result.isLeft());
        assertEquals("Error", result.getLeft());
    }

    // Tests for integration with Try
    @Test
    void toTry_returnsSuccessForRight() {
        Either<String, Integer> right = Either.right(42);
        Try<Integer> result = right.toTry();
        assertTrue(result.isSuccess());
        assertEquals(Integer.valueOf(42), result.get());
    }

    @Test
    void toTry_returnsFailureForLeft() {
        Either<String, Integer> left = Either.left("Error");
        Try<Integer> result = left.toTry();
        assertTrue(result.isFailure());
        assertTrue(result.getFailure() instanceof NoSuchElementException);
    }

    @Test
    void fromTry_createsRightForSuccess() {
        Try<Integer> success = Try.success(42);
        Either<String, Integer> result = Either.fromTry(success, Throwable::getMessage);
        assertTrue(result.isRight());
        assertEquals(Integer.valueOf(42), result.getRight());
    }

    @Test
    void fromTry_createsLeftForFailure() {
        Try<Integer> failure = Try.failure(new RuntimeException("Error"));
        Either<String, Integer> result = Either.fromTry(failure, Throwable::getMessage);
        assertTrue(result.isLeft());
        assertEquals("Error", result.getLeft());
    }
}