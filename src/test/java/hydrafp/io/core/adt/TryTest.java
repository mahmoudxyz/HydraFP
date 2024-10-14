package hydrafp.io.core.adt;

import org.junit.jupiter.api.Test;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicInteger;
import static org.junit.jupiter.api.Assertions.*;

class TryTest {

    @Test
    void of_returnsSuccessForNonThrowingSupplier() {
        Try<Integer> result = Try.of(() -> 1 + 1);
        assertTrue(result.isSuccess());
    }

    @Test
    void of_returnsFailureForThrowingSupplier() {
        Try<Integer> result = Try.of(() -> 1 / 0);
        assertTrue(result.isFailure());
    }

    @Test
    void success_createsSuccessInstance() {
        Try<Integer> result = Try.success(42);
        assertTrue(result.isSuccess());
    }

    @Test
    void failure_createsFailureInstance() {
        Try<Integer> result = Try.failure(new RuntimeException("Test"));
        assertTrue(result.isFailure());
    }

    @Test
    void isSuccess_returnsTrueForSuccess() {
        Try<Integer> success = Try.success(42);
        assertTrue(success.isSuccess());
    }

    @Test
    void isSuccess_returnsFalseForFailure() {
        Try<Integer> failure = Try.failure(new RuntimeException());
        assertFalse(failure.isSuccess());
    }

    @Test
    void isFailure_returnsTrueForFailure() {
        Try<Integer> failure = Try.failure(new RuntimeException());
        assertTrue(failure.isFailure());
    }

    @Test
    void isFailure_returnsFalseForSuccess() {
        Try<Integer> success = Try.success(42);
        assertFalse(success.isFailure());
    }

    @Test
    void get_returnsValueForSuccess() {
        Try<Integer> success = Try.success(42);
        assertEquals(42, success.get());
    }

    @Test
    void get_throwsExceptionForFailure() {
        Try<Integer> failure = Try.failure(new RuntimeException("Test"));
        assertThrows(RuntimeException.class, failure::get);
    }

    @Test
    void getFailure_returnsExceptionForFailure() {
        RuntimeException exception = new RuntimeException("Test");
        Try<Integer> failure = Try.failure(exception);
        assertEquals(exception, failure.getFailure());
    }

    @Test
    void getFailure_throwsUnsupportedOperationExceptionForSuccess() {
        Try<Integer> success = Try.success(42);
        assertThrows(UnsupportedOperationException.class, success::getFailure);
    }

    @Test
    void map_appliesFunctionForSuccess() {
        Try<Integer> success = Try.success(21);
        Try<Integer> result = success.map(x -> x * 2);
        assertEquals(42, result.get());
    }

    @Test
    void map_preservesFailureForFailure() {
        RuntimeException exception = new RuntimeException("Test");
        Try<Integer> failure = Try.failure(exception);
        Try<Integer> result = failure.map(x -> x * 2);
        assertEquals(exception, result.getFailure());
    }

    @Test
    void flatMap_appliesFunctionForSuccess() {
        Try<Integer> success = Try.success(21);
        Try<Integer> result = success.flatMap(x -> Try.success(x * 2));
        assertEquals(42, result.get());
    }

    @Test
    void flatMap_preservesFailureForFailure() {
        RuntimeException exception = new RuntimeException("Test");
        Try<Integer> failure = Try.failure(exception);
        Try<Integer> result = failure.flatMap(x -> Try.success(x * 2));
        assertEquals(exception, result.getFailure());
    }

    @Test
    void recover_appliesFunctionForFailure() {
        Try<Integer> failure = Try.failure(new RuntimeException());
        Try<Integer> result = failure.recover(ex -> 42);
        assertEquals(42, result.get());
    }

    @Test
    void recover_preservesSuccessForSuccess() {
        Try<Integer> success = Try.success(21);
        Try<Integer> result = success.recover(ex -> 42);
        assertEquals(21, result.get());
    }

    @Test
    void recoverWith_appliesFunctionForFailure() {
        Try<Integer> failure = Try.failure(new RuntimeException());
        Try<Integer> result = failure.recoverWith(ex -> Try.success(42));
        assertEquals(42, result.get());
    }

    @Test
    void recoverWith_preservesSuccessForSuccess() {
        Try<Integer> success = Try.success(21);
        Try<Integer> result = success.recoverWith(ex -> Try.success(42));
        assertEquals(21, result.get());
    }

    @Test
    void getOrElse_returnsValueForSuccess() {
        Try<Integer> success = Try.success(21);
        assertEquals(21, success.getOrElse(42));
    }

    @Test
    void getOrElse_returnsDefaultForFailure() {
        Try<Integer> failure = Try.failure(new RuntimeException());
        assertEquals(42, failure.getOrElse(42));
    }

    @Test
    void orElse_returnsOriginalForSuccess() {
        Try<Integer> success = Try.success(21);
        Try<Integer> result = success.orElse(() -> Try.success(42));
        assertEquals(21, result.get());
    }

    @Test
    void orElse_returnsAlternativeForFailure() {
        Try<Integer> failure = Try.failure(new RuntimeException());
        Try<Integer> result = failure.orElse(() -> Try.success(42));
        assertEquals(42, result.get());
    }

    @Test
    void fold_appliesSuccessMapperForSuccess() {
        Try<Integer> success = Try.success(21);
        Integer result = success.fold(ex -> 0, x -> x * 2);
        assertEquals(42, result);
    }

    @Test
    void fold_appliesFailureMapperForFailure() {
        Try<Integer> failure = Try.failure(new RuntimeException("Test"));
        String result = failure.fold(Throwable::getMessage, Object::toString);
        assertEquals("Test", result);
    }

    @Test
    void filter_returnsSuccessWhenPredicateIsTrue() {
        Try<Integer> success = Try.success(42);
        Try<Integer> result = success.filter(x -> x > 20);
        assertTrue(result.isSuccess());
    }

    @Test
    void filter_returnsFailureWhenPredicateIsFalse() {
        Try<Integer> success = Try.success(10);
        Try<Integer> result = success.filter(x -> x > 20);
        assertTrue(result.isFailure());
    }

    @Test
    void toOption_returnsSomeForSuccess() {
        Try<Integer> success = Try.success(42);
        Option<Integer> result = success.toOption();
        assertTrue(result.isDefined());
    }

    @Test
    void toOption_returnsNoneForFailure() {
        Try<Integer> failure = Try.failure(new RuntimeException());
        Option<Integer> result = failure.toOption();
        assertTrue(result.isEmpty());
    }

    @Test
    void fromOption_returnsTrySuccessForSome() {
        Option<Integer> some = Option.some(42);
        Try<Integer> result = Try.fromOption(some, NoSuchElementException::new);
        assertTrue(result.isSuccess());
    }

    @Test
    void fromOption_returnsTryFailureForNone() {
        Option<Integer> none = Option.none();
        Try<Integer> result = Try.fromOption(none, () -> new NoSuchElementException("Test"));
        assertTrue(result.isFailure());
    }

    @Test
    void flatMapOption_appliesFunctionForSuccess() {
        Try<Integer> success = Try.success(21);
        Try<Integer> result = success.flatMapOption(x -> Option.some(x * 2));
        assertEquals(42, result.get());
    }

    @Test
    void flatMapOption_returnsFailureForNone() {
        Try<Integer> success = Try.success(21);
        Try<Integer> result = success.flatMapOption(x -> Option.none());
        assertTrue(result.isFailure());
    }

    @Test
    void onSuccess_executesActionForSuccess() {
        AtomicInteger counter = new AtomicInteger(0);
        Try<Integer> success = Try.success(42);
        success.onSuccess(x -> counter.incrementAndGet());
        assertEquals(1, counter.get());
    }

    @Test
    void onSuccess_doesNotExecuteActionForFailure() {
        AtomicInteger counter = new AtomicInteger(0);
        Try<Integer> failure = Try.failure(new RuntimeException());
        failure.onSuccess(x -> counter.incrementAndGet());
        assertEquals(0, counter.get());
    }

    @Test
    void onFailure_executesActionForFailure() {
        AtomicInteger counter = new AtomicInteger(0);
        Try<Integer> failure = Try.failure(new RuntimeException());
        failure.onFailure(ex -> counter.incrementAndGet());
        assertEquals(1, counter.get());
    }

    @Test
    void onFailure_doesNotExecuteActionForSuccess() {
        AtomicInteger counter = new AtomicInteger(0);
        Try<Integer> success = Try.success(42);
        success.onFailure(ex -> counter.incrementAndGet());
        assertEquals(0, counter.get());
    }

    @Test
    void transform_appliesSuccessMapperForSuccess() {
        Try<Integer> success = Try.success(21);
        String result = success.transform(x -> Integer.toString(x * 2), Throwable::getMessage);
        assertEquals("42", result);
    }

    @Test
    void transform_appliesFailureMapperForFailure() {
        Try<Integer> failure = Try.failure(new RuntimeException("Test"));
        String result = failure.transform(Object::toString, Throwable::getMessage);
        assertEquals("Test", result);
    }

    @Test
    void transform_throwsNullPointerExceptionWhenSuccessTransformIsNull() {
        Try<Integer> success = Try.success(21);
        assertThrows(NullPointerException.class, () -> success.transform(null, Throwable::getMessage));
    }

    @Test
    void transform_throwsNullPointerExceptionWhenFailureTransformIsNull() {
        Try<Integer> failure = Try.failure(new RuntimeException("Test"));
        assertThrows(NullPointerException.class, () -> failure.transform(Object::toString, null));
    }

    @Test
    void toEither_returnsRightForSuccess() {
        Try<Integer> success = Try.success(42);
        Either<String, Integer> result = success.toEither(Throwable::getMessage);
        assertTrue(result.isRight());
    }

    @Test
    void toEither_returnsLeftForFailure() {
        Try<Integer> failure = Try.failure(new RuntimeException("Error"));
        Either<String, Integer> result = failure.toEither(Throwable::getMessage);
        assertTrue(result.isLeft());
    }

    @Test
    void toEither_appliesLeftMapperForFailure() {
        Try<Integer> failure = Try.failure(new RuntimeException("Error"));
        Either<String, Integer> result = failure.toEither(Throwable::getMessage);
        assertEquals("Error", result.getLeft());
    }

    @Test
    void fromEither_returnsTrySuccessForRight() {
        Either<Throwable, Integer> right = Either.right(42);
        Try<Integer> result = Try.fromEither(right);
        assertTrue(result.isSuccess());
    }

    @Test
    void fromEither_returnsTryFailureForLeft() {
        Either<Throwable, Integer> left = Either.left(new RuntimeException("Error"));
        Try<Integer> result = Try.fromEither(left);
        assertTrue(result.isFailure());
    }

    @Test
    void fromEither_preservesExceptionForLeft() {
        RuntimeException exception = new RuntimeException("Error");
        Either<Throwable, Integer> left = Either.left(exception);
        Try<Integer> result = Try.fromEither(left);
        assertEquals(exception, result.getFailure());
    }

    @Test
    void fromEither_throwsNullPointerExceptionForNullEither() {
        assertThrows(NullPointerException.class, () -> Try.fromEither(null));
    }
}