package hydrafp.io.core.collections;

import hydrafp.io.core.adt.Option;
import hydrafp.io.core.adt.Either;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class ImmutableListTest {

    @Test
    @DisplayName("empty() should return an empty list")
    void testEmpty() {
        assertTrue(ImmutableList.empty().isEmpty());
    }

    @Test
    @DisplayName("of() should create a non-empty list from varargs")
    void testOf() {
        assertEquals(3, ImmutableList.of(1, 2, 3).size());
    }

    @Test
    @DisplayName("cons() should add an element to the front of the list")
    void testCons() {
        assertEquals(Option.some(1), ImmutableList.empty().cons(1).head());
    }

    @Test
    @DisplayName("head() should return Option.none() for an empty list")
    void testHeadOnEmptyList() {
        assertEquals(Option.none(), ImmutableList.empty().head());
    }

    @Test
    @DisplayName("head() should return Option.some() with the first element for a non-empty list")
    void testHeadOnNonEmptyList() {
        assertEquals(Option.some(1), ImmutableList.of(1, 2, 3).head());
    }

    @Test
    @DisplayName("tail() of an empty list should be an empty list")
    void testTailOnEmptyList() {
        assertTrue(ImmutableList.empty().tail().isEmpty());
    }

    @Test
    @DisplayName("tail() of a non-empty list should return a list without the first element")
    void testTailOnNonEmptyList() {
        assertEquals(Option.some(2), ImmutableList.of(1, 2, 3).tail().head());
    }

    @Test
    @DisplayName("isEmpty() should return true for an empty list")
    void testIsEmptyOnEmptyList() {
        assertTrue(ImmutableList.empty().isEmpty());
    }

    @Test
    @DisplayName("isEmpty() should return false for a non-empty list")
    void testIsEmptyOnNonEmptyList() {
        assertFalse(ImmutableList.of(1).isEmpty());
    }

    @Test
    @DisplayName("size() should return 0 for an empty list")
    void testSizeOnEmptyList() {
        assertEquals(0, ImmutableList.empty().size());
    }

    @Test
    @DisplayName("size() should return the correct number of elements for a non-empty list")
    void testSizeOnNonEmptyList() {
        assertEquals(3, ImmutableList.of(1, 2, 3).size());
    }

    @Test
    @DisplayName("map() should transform each element of the list")
    void testMap() {
        assertEquals(Option.some(2), ImmutableList.of(1, 2, 3).map(x -> x * 2).head());
    }

    @Test
    @DisplayName("flatMap() should flatten and transform the list")
    void testFlatMap() {
        ImmutableList<Integer> result = ImmutableList.of(1, 2, 3)
                .flatMap(x -> ImmutableList.of(x, x * 2));
        assertEquals(Option.some(1), result.head());
        assertEquals(6, result.size());
    }

    @Test
    @DisplayName("filter() should keep only elements that satisfy the predicate")
    void testFilter() {
        assertEquals(Option.some(2), ImmutableList.of(1, 2, 3, 4).filter(x -> x % 2 == 0).head());
    }

    @Test
    @DisplayName("appendAll() should combine two lists")
    void testAppendAll() {
        ImmutableList<Integer> result = ImmutableList.of(1, 2).appendAll(ImmutableList.of(3, 4));
        assertEquals(4, result.size());
        assertEquals(Option.some(1), result.head());
    }

    @Test
    @DisplayName("get() should return Either.right with the element at the specified index")
    void testGetValidIndex() {
        assertEquals(Either.right(2), ImmutableList.of(1, 2, 3).get(1));
    }

    @Test
    @DisplayName("get() should return Either.left with IndexOutOfBoundsException for invalid index")
    void testGetInvalidIndex() {
        assertTrue(ImmutableList.of(1, 2, 3).get(3).isLeft());
    }

    @Test
    @DisplayName("getAsTry() should return Success with the element at the specified index")
    void testGetAsTryValidIndex() {
        assertTrue(ImmutableList.of(1, 2, 3).getAsTry(1).isSuccess());
    }

    @Test
    @DisplayName("getAsTry() should return Failure for invalid index")
    void testGetAsTryInvalidIndex() {
        assertTrue(ImmutableList.of(1, 2, 3).getAsTry(3).isFailure());
    }

    @Test
    @DisplayName("getAsOption() should return Some with the element at the specified index")
    void testGetAsOptionValidIndex() {
        assertEquals(Option.some(2), ImmutableList.of(1, 2, 3).getAsOption(1));
    }

    @Test
    @DisplayName("getAsOption() should return None for invalid index")
    void testGetAsOptionInvalidIndex() {
        assertEquals(Option.none(), ImmutableList.of(1, 2, 3).getAsOption(3));
    }

    @Test
    @DisplayName("asIterator() should allow iterating over the list elements")
    void testAsIterator() {
        ImmutableList<Integer> list = ImmutableList.of(1, 2, 3);
        List<Integer> result = list.asIterator().collect(Collectors.toList());
        assertEquals(List.of(1, 2, 3), result);
    }

    @Test
    @DisplayName("iterator() should return None when next() is called on an exhausted iterator")
    void testFIteratorExhausted() {
        ImmutableList<Integer> list = ImmutableList.of(1);
        var iterator = list.iterator();
        iterator.next();
        assertEquals(Option.none(), iterator.next());
    }

    @Test
    @DisplayName("toString() should return a string representation of the list")
    void testToString() {
        assertEquals("Cons(1, Cons(2, Cons(3, Nil)))", ImmutableList.of(1, 2, 3).toString());
    }

    @Test
    @DisplayName("toReadableString() should return a readable representation of the list")
    void testToReadableString() {
        assertEquals("[1, 2, 3]", ImmutableList.of(1, 2, 3).toReadableString());
    }

    @Test
    @DisplayName("foldLeft() should accumulate values from left to right")
    void testFoldLeft() {
        int sum = ImmutableList.of(1, 2, 3, 4, 5)
                .foldLeft(0, Integer::sum);
        assertEquals(15, sum);
    }

    @Test
    @DisplayName("foldRight() should accumulate values from right to left")
    void testFoldRight() {
        String result = ImmutableList.of(1, 2, 3, 4, 5)
                .foldRight("", (x, acc) -> x + acc);
        assertEquals("12345", result);
    }

    @Test
    @DisplayName("reverse() should reverse the order of elements in the list")
    void testReverse() {
        ImmutableList<Integer> reversed = ImmutableList.of(1, 2, 3).reverse();
        assertEquals(Option.some(3), reversed.head());
    }

    @Test
    @DisplayName("take() should return a list with the first n elements")
    void testTake() {
        assertEquals(2, ImmutableList.of(1, 2, 3, 4, 5).take(2).size());
    }

    @Test
    @DisplayName("drop() should return a list without the first n elements")
    void testDrop() {
        assertEquals(Option.some(3), ImmutableList.of(1, 2, 3, 4, 5).drop(2).head());
    }

    @Test
    @DisplayName("zip() should combine two lists element-wise")
    void testZip() {
        ImmutableList<String> list1 = ImmutableList.of("a", "b", "c");
        ImmutableList<Integer> list2 = ImmutableList.of(1, 2, 3);
        ImmutableList<String> zipped = list1.zip(list2, (s, i) -> s + i);
        assertEquals(Option.some("a1"), zipped.head());
    }

    @Test
    @DisplayName("collect() should transform and filter elements in one pass")
    void testCollect() {
        ImmutableList<String> result = ImmutableList.of(1, 2, 3, 4, 5)
                .collect(x -> x % 2 == 0 ? Option.some(x.toString()) : Option.none());
        assertEquals(Option.some("2"), result.head());
    }


    @Test
    @DisplayName("cons() should not modify the original list")
    void testConsImmutability() {
        ImmutableList<Integer> original = ImmutableList.of(1, 2, 3);
        ImmutableList<Integer> modified = original.cons(0);

        assertNotSame(original, modified);
        assertEquals(Option.some(1), original.head());
        assertEquals(Option.some(0), modified.head());
    }

    @Test
    @DisplayName("map() should not modify the original list")
    void testMapImmutability() {
        ImmutableList<Integer> original = ImmutableList.of(1, 2, 3);
        ImmutableList<Integer> modified = original.map(x -> x * 2);

        assertNotSame(original, modified);
        assertEquals(Option.some(1), original.head());
        assertEquals(Option.some(2), modified.head());
    }

    @Test
    @DisplayName("filter() should not modify the original list")
    void testFilterImmutability() {
        ImmutableList<Integer> original = ImmutableList.of(1, 2, 3, 4);
        ImmutableList<Integer> modified = original.filter(x -> x % 2 == 0);

        assertNotSame(original, modified);
        assertEquals(Option.some(1), original.head());
        assertEquals(Option.some(2), modified.head());
    }

    @Test
    @DisplayName("appendAll() should not modify the original lists")
    void testAppendAllImmutability() {
        ImmutableList<Integer> original1 = ImmutableList.of(1, 2);
        ImmutableList<Integer> original2 = ImmutableList.of(3, 4);
        ImmutableList<Integer> combined = original1.appendAll(original2);

        assertNotSame(original1, combined);
        assertNotSame(original2, combined);
        assertEquals(Option.some(1), original1.head());
        assertEquals(Option.some(3), original2.head());
        assertEquals(Option.some(1), combined.head());
    }

    @Test
    @DisplayName("reverse() should not modify the original list")
    void testReverseImmutability() {
        ImmutableList<Integer> original = ImmutableList.of(1, 2, 3);
        ImmutableList<Integer> reversed = original.reverse();

        assertNotSame(original, reversed);
        assertEquals(Option.some(1), original.head());
        assertEquals(Option.some(3), reversed.head());
    }

    @Test
    @DisplayName("take() should not modify the original list")
    void testTakeImmutability() {
        ImmutableList<Integer> original = ImmutableList.of(1, 2, 3, 4, 5);
        ImmutableList<Integer> taken = original.take(3);

        assertNotSame(original, taken);
        assertEquals(5, original.size());
        assertEquals(3, taken.size());
        assertEquals(Option.some(1), original.head());
        assertEquals(Option.some(1), taken.head());
    }

    @Test
    @DisplayName("drop() should not modify the original list")
    void testDropImmutability() {
        ImmutableList<Integer> original = ImmutableList.of(1, 2, 3, 4, 5);
        ImmutableList<Integer> dropped = original.drop(2);

        assertNotSame(original, dropped);
        assertEquals(5, original.size());
        assertEquals(3, dropped.size());
        assertEquals(Option.some(1), original.head());
        assertEquals(Option.some(3), dropped.head());
    }

}