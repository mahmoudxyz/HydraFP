package hydrafp.io.core.tagged;

import hydrafp.io.core.adt.Try;
import hydrafp.io.core.adt.Either;
import hydrafp.io.core.adt.Option;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

class TaggedTest {

    @Nested
    class Creation {
        @Test
        void testOf() {

            Tagged<String, Integer> tagged = Tagged.of(42);
            assertEquals(42, tagged.getValue());
        }

        @Test
        void testFromOption() {
            Option<Integer> some = Option.some(42);
            Option<Tagged<String, Integer>> taggedSome = Tagged.fromOption(some);
            assertTrue(taggedSome.isDefined());
            assertEquals(42, taggedSome.get().getValue());

            Option<Integer> none = Option.none();
            Option<Tagged<String, Integer>> taggedNone = Tagged.fromOption(none);
            assertTrue(taggedNone.isEmpty());
        }

        @Test
        void testFromTry() {
            Try<Integer> success = Try.success(42);
            Try<Tagged<String, Integer>> taggedSuccess = Tagged.fromTry(success);
            assertTrue(taggedSuccess.isSuccess());
            assertEquals(42, taggedSuccess.get().getValue());

            Try<Integer> failure = Try.failure(new RuntimeException("Test"));
            Try<Tagged<String, Integer>> taggedFailure = Tagged.fromTry(failure);
            assertTrue(taggedFailure.isFailure());
        }

        @Test
        void testFromEither() {
            Either<String, Integer> right = Either.right(42);
            Either<String, Tagged<Boolean, Integer>> taggedRight = Tagged.fromEither(right);
            assertTrue(taggedRight.isRight());
            assertEquals(42, taggedRight.getRight().getValue());

            Either<String, Integer> left = Either.left("Error");
            Either<String, Tagged<Boolean, Integer>> taggedLeft = Tagged.fromEither(left);
            assertTrue(taggedLeft.isLeft());
            assertEquals("Error", taggedLeft.getLeft());
        }
    }

    @Nested
    class Transformation {
        @Test
        void testMap() {
            Tagged<String, Integer> tagged = Tagged.of(42);
            Tagged<String, String> mapped = tagged.map(Object::toString);
            assertEquals("42", mapped.getValue());
        }

        @Test
        void testRetag() {
            Tagged<String, Integer> tagged = Tagged.of(42);
            Tagged<Boolean, Integer> retagged = tagged.retag();
            assertEquals(42, retagged.getValue());
        }

        @Test
        void testFlatMap() {
            Tagged<String, Integer> tagged = Tagged.of(42);
            Tagged<String, String> flatMapped = tagged.flatMap(i -> Tagged.of(i.toString()));
            assertEquals("42", flatMapped.getValue());
        }

        @Test
        void testAp() {
            Tagged<String, Integer> tagged = Tagged.of(42);
            Tagged<String, Function<Integer, String>> taggedFunction = Tagged.of(Object::toString);
            Tagged<String, String> result = tagged.ap(taggedFunction);
            assertEquals("42", result.getValue());
        }

        @Test
        void testZip() {
            Tagged<String, Integer> t1 = Tagged.of(42);
            Tagged<String, String> t2 = Tagged.of("Hello");
            Tagged<String, String> zipped = t1.zip(t2, (i, s) -> s + i);
            assertEquals("Hello42", zipped.getValue());
        }

        @Test
        void testMap2() {
            Tagged<String, Integer> t1 = Tagged.of(42);
            Tagged<String, String> t2 = Tagged.of("Hello");
            Tagged<String, String> result = Tagged.map2(t1, t2, (i, s) -> s + i);
            assertEquals("Hello42", result.getValue());
        }
    }

    @Nested
    class Conversion {
        @Test
        void testToOption() {
            Tagged<String, Integer> tagged = Tagged.of(42);
            Option<Integer> option = tagged.toOption();
            assertTrue(option.isDefined());
            assertEquals(42, option.get());
        }

        @Test
        void testToTry() {
            Tagged<String, Integer> tagged = Tagged.of(42);
            Try<Integer> tryResult = tagged.toTry();
            assertTrue(tryResult.isSuccess());
            assertEquals(42, tryResult.get());
        }

        @Test
        void testToEither() {
            Tagged<String, Integer> tagged = Tagged.of(42);
            Either<String, Integer> either = tagged.toEither(() -> "Error");
            assertTrue(either.isRight());
            assertEquals(42, either.getRight());
        }
    }

    @Nested
    class Filtering {
        @Test
        void testFilter() {
            Tagged<String, Integer> tagged = Tagged.of(42);
            Tagged<String, Integer> filtered = tagged.filter(i -> i > 0);
            assertNotNull(filtered);
            assertEquals(42, filtered.getValue());

            Tagged<String, Integer> filteredOut = tagged.filter(i -> i < 0);
            assertNull(filteredOut);
        }

        @Test
        void testFilterToOption() {
            Tagged<String, Integer> tagged = Tagged.of(42);
            Option<Tagged<String, Integer>> filtered = tagged.filterToOption(i -> i > 0);
            assertTrue(filtered.isDefined());
            assertEquals(42, filtered.get().getValue());

            Option<Tagged<String, Integer>> filteredOut = tagged.filterToOption(i -> i < 0);
            assertTrue(filteredOut.isEmpty());
        }
    }

    @Nested
    class SideEffects {
        @Test
        void testPeek() {
            AtomicInteger counter = new AtomicInteger(0);
            Tagged<String, Integer> tagged = Tagged.of(42);
            Tagged<String, Integer> peeked = tagged.peek(i -> counter.set(i));
            assertEquals(42, peeked.getValue());
            assertEquals(42, counter.get());
        }

        @Test
        void testPeekOrThrow() {
            AtomicInteger counter = new AtomicInteger(0);
            Tagged<String, Integer> tagged = Tagged.of(42);
            assertDoesNotThrow(() -> {
                Tagged<String, Integer> peeked = tagged.peekOrThrow(i -> counter.set(i));
                assertEquals(42, peeked.getValue());
                assertEquals(42, counter.get());
            });

            assertThrows(Exception.class, () ->
                    tagged.peekOrThrow(i -> { throw new Exception("Test"); })
            );
        }
    }

    @Nested
    class Miscellaneous {
        @Test
        void testMatch() {
            Tagged<String, Integer> tagged = Tagged.of(42);
            String result = tagged.match(i -> "Number: " + i);
            assertEquals("Number: 42", result);
        }

        @Test
        void testPure() {
            Tagged<String, Integer> tagged = Tagged.pure(42);
            assertEquals(42, tagged.getValue());

            Tagged<Boolean, String> taggedString = Tagged.pure("Hello");
            assertEquals("Hello", taggedString.getValue());
        }


        @Test
        void testEquals() {
            Tagged<String, Integer> t1 = Tagged.of(42);
            Tagged<String, Integer> t2 = Tagged.of(42);
            Tagged<Boolean, Integer> t3 = Tagged.of(42);
            Tagged<String, Integer> t4 = Tagged.of(24);

            assertEquals(t1, t2);
            assertEquals(t1, t3);
            assertNotEquals(t1, t4);
        }

        @Test
        void testHashCode() {
            Tagged<String, Integer> t1 = Tagged.of(42);
            Tagged<String, Integer> t2 = Tagged.of(42);
            assertEquals(t1.hashCode(), t2.hashCode());
        }

        @Test
        void testToString() {
            Tagged<String, Integer> tagged = Tagged.of(42);
            assertEquals("Tagged[42]", tagged.toString());
        }
    }


}