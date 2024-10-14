package hydrafp.io.core.adt;

import org.junit.jupiter.api.Test;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicInteger;
import static org.junit.jupiter.api.Assertions.*;

class OptionTest {

    @Test
    void of_returnsSomeForNonNullValue() {
        Option<String> result = Option.of("test");
        assertTrue(result.isDefined());
    }

    @Test
    void of_returnsNoneForNullValue() {
        Option<String> result = Option.of(null);
        assertTrue(result.isEmpty());
    }

    @Test
    void some_createsSomeInstance() {
        Option<Integer> result = Option.some(42);
        assertTrue(result.isDefined());
    }

    @Test
    void none_createsNoneInstance() {
        Option<Integer> result = Option.none();
        assertTrue(result.isEmpty());
    }

    @Test
    void flatMap_appliesFunctionForSome() {
        Option<Integer> some = Option.some(21);
        Option<Integer> result = some.flatMap(x -> Option.some(x * 2));
        assertEquals(Integer.valueOf(42), result.get());
    }

    @Test
    void flatMap_returnsNoneForNone() {
        Option<Integer> none = Option.none();
        Option<Integer> result = none.flatMap(x -> Option.some(x * 2));
        assertTrue(result.isEmpty());
    }

    @Test
    void map_appliesFunctionForSome() {
        Option<Integer> some = Option.some(21);
        Option<Integer> result = some.map(x -> x * 2);
        assertEquals(Integer.valueOf(42), result.get());
    }

    @Test
    void map_returnsNoneForNone() {
        Option<Integer> none = Option.none();
        Option<Integer> result = none.map(x -> x * 2);
        assertTrue(result.isEmpty());
    }

    @Test
    void getOrElse_returnsValueForSome() {
        Option<Integer> some = Option.some(42);
        assertEquals(Integer.valueOf(42), some.getOrElse(0));
    }

    @Test
    void getOrElse_returnsDefaultForNone() {
        Option<Integer> none = Option.none();
        assertEquals(Integer.valueOf(0), none.getOrElse(0));
    }

    @Test
    void getOrElse_withSupplier_returnsValueForSome() {
        Option<Integer> some = Option.some(42);
        assertEquals(Integer.valueOf(42), some.getOrElse(() -> 0));
    }

    @Test
    void getOrElse_withSupplier_returnsDefaultForNone() {
        Option<Integer> none = Option.none();
        assertEquals(Integer.valueOf(0), none.getOrElse(() -> 0));
    }

    @Test
    void isEmpty_returnsFalseForSome() {
        Option<Integer> some = Option.some(42);
        assertFalse(some.isEmpty());
    }

    @Test
    void isEmpty_returnsTrueForNone() {
        Option<Integer> none = Option.none();
        assertTrue(none.isEmpty());
    }

    @Test
    void isDefined_returnsTrueForSome() {
        Option<Integer> some = Option.some(42);
        assertTrue(some.isDefined());
    }

    @Test
    void isDefined_returnsFalseForNone() {
        Option<Integer> none = Option.none();
        assertFalse(none.isDefined());
    }

    @Test
    void forEach_executesActionForSome() {
        Option<Integer> some = Option.some(42);
        AtomicInteger counter = new AtomicInteger(0);
        some.forEach(x -> counter.incrementAndGet());
        assertEquals(1, counter.get());
    }

    @Test
    void forEach_doesNotExecuteActionForNone() {
        Option<Integer> none = Option.none();
        AtomicInteger counter = new AtomicInteger(0);
        none.forEach(x -> counter.incrementAndGet());
        assertEquals(0, counter.get());
    }

    @Test
    void get_returnsValueForSome() {
        Option<Integer> some = Option.some(42);
        assertEquals(Integer.valueOf(42), some.get());
    }

    @Test
    void get_throwsNoSuchElementExceptionForNone() {
        Option<Integer> none = Option.none();
        assertThrows(NoSuchElementException.class, none::get);
    }

    @Test
    void filter_returnsSomeWhenPredicateIsTrue() {
        Option<Integer> some = Option.some(42);
        Option<Integer> result = some.filter(x -> x > 20);
        assertTrue(result.isDefined());
    }

    @Test
    void filter_returnsNoneWhenPredicateIsFalse() {
        Option<Integer> some = Option.some(10);
        Option<Integer> result = some.filter(x -> x > 20);
        assertTrue(result.isEmpty());
    }

    @Test
    void filter_returnsNoneForNone() {
        Option<Integer> none = Option.none();
        Option<Integer> result = none.filter(x -> x > 20);
        assertTrue(result.isEmpty());
    }

    @Test
    void orElse_returnsSomeForSome() {
        Option<Integer> some = Option.some(42);
        Option<Integer> result = some.orElse(() -> Option.some(0));
        assertEquals(Integer.valueOf(42), result.get());
    }

    @Test
    void orElse_returnsAlternativeForNone() {
        Option<Integer> none = Option.none();
        Option<Integer> result = none.orElse(() -> Option.some(42));
        assertEquals(Integer.valueOf(42), result.get());
    }

    @Test
    void stream_returnsStreamWithOneElementForSome() {
        Option<Integer> some = Option.some(42);
        assertEquals(1, some.stream().count());
    }

    @Test
    void stream_returnsEmptyStreamForNone() {
        Option<Integer> none = Option.none();
        assertEquals(0, none.stream().count());
    }

    @Test
    void toOptional_returnsOptionalWithValueForSome() {
        Option<Integer> some = Option.some(42);
        assertTrue(some.toOptional().isPresent());
    }

    @Test
    void toOptional_returnsEmptyOptionalForNone() {
        Option<Integer> none = Option.none();
        assertTrue(none.toOptional().isEmpty());
    }

    @Test
    void equals_returnsTrueForTwoSomesWithSameValue() {
        Option<Integer> some1 = Option.some(42);
        Option<Integer> some2 = Option.some(42);
        assertEquals(some1, some2);
    }

    @Test
    void equals_returnsFalseForTwoSomesWithDifferentValues() {
        Option<Integer> some1 = Option.some(42);
        Option<Integer> some2 = Option.some(24);
        assertNotEquals(some1, some2);
    }

    @Test
    void equals_returnsTrueForTwoNones() {
        Option<Integer> none1 = Option.none();
        Option<Integer> none2 = Option.none();
        assertEquals(none1, none2);
    }

    @Test
    void hashCode_returnsSameValueForTwoSomesWithSameValue() {
        Option<Integer> some1 = Option.some(42);
        Option<Integer> some2 = Option.some(42);
        assertEquals(some1.hashCode(), some2.hashCode());
    }

    @Test
    void hashCode_returnsSameValueForTwoNones() {
        Option<Integer> none1 = Option.none();
        Option<Integer> none2 = Option.none();
        assertEquals(none1.hashCode(), none2.hashCode());
    }

    @Test
    void toString_returnsCorrectRepresentationForSome() {
        Option<Integer> some = Option.some(42);
        assertEquals("Some(42)", some.toString());
    }

    @Test
    void toString_returnsCorrectRepresentationForNone() {
        Option<Integer> none = Option.none();
        assertEquals("None", none.toString());
    }
}