package core.adt;

import hydrafp.io.core.adt.List;
import hydrafp.io.core.adt.NonEmptyList;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class NonEmptyListTest {

    @Test
    void testNonEmptyListCreation() {
        NonEmptyList<Integer> nel = NonEmptyList.of(1, 2, 3);
        assertNotNull(nel);
    }

    @Test
    void testNonEmptyListHead() {
        NonEmptyList<Integer> nel = NonEmptyList.of(1, 2, 3);
        assertEquals(1, nel.head());
    }

    @Test
    void testNonEmptyListTail() {
        NonEmptyList<Integer> nel = NonEmptyList.of(1, 2, 3);
        assertEquals("Cons(2, Cons(3, Nil))", nel.tail().toString());
    }

    @Test
    void testNonEmptyListMap() {
        NonEmptyList<Integer> nel = NonEmptyList.of(1, 2, 3);
        NonEmptyList<Integer> mappedNel = nel.map(x -> x * 2);
        assertEquals(2, mappedNel.head());
    }

    @Test
    void testNonEmptyListMapTail() {
        NonEmptyList<Integer> nel = NonEmptyList.of(1, 2, 3);
        NonEmptyList<Integer> mappedNel = nel.map(x -> x * 2);
        assertEquals("Cons(4, Cons(6, Nil))", mappedNel.tail().toString());
    }

    @Test
    void testNonEmptyListToList() {
        NonEmptyList<Integer> nel = NonEmptyList.of(1, 2, 3);
        List<Integer> list = nel.toList();
        assertEquals("Cons(1, Cons(2, Cons(3, Nil)))", list.toString());
    }

    @Test
    void testNonEmptyListToString() {
        NonEmptyList<Integer> nel = NonEmptyList.of(1, 2, 3);
        assertEquals("NonEmptyList(1, Cons(2, Cons(3, Nil)))", nel.toString());
    }

    @Test
    void testNonEmptyListWithSingleElement() {
        NonEmptyList<Integer> nel = NonEmptyList.of(1);
        assertEquals(1, nel.head());
    }

    @Test
    void testNonEmptyListWithSingleElementTail() {
        NonEmptyList<Integer> nel = NonEmptyList.of(1);
        assertTrue(nel.tail().isEmpty());
    }

    @Test
    void testNonEmptyListMapWithSingleElement() {
        NonEmptyList<Integer> nel = NonEmptyList.of(1);
        NonEmptyList<String> mappedNel = nel.map(Object::toString);
        assertEquals("1", mappedNel.head());
    }
}