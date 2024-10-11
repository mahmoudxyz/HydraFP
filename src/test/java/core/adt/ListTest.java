package core.adt;

import hydrafp.io.core.adt.List;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ListTest {

    @Test
    void testEmptyListCreation() {
        List<Integer> emptyList = List.empty();
        assertTrue(emptyList.isEmpty());
    }

    @Test
    void testNonEmptyListCreation() {
        List<Integer> list = List.of(1, 2, 3);
        assertFalse(list.isEmpty());
    }

    @Test
    void testListSize() {
        List<Integer> list = List.of(1, 2, 3);
        assertEquals(3, list.size());
    }

    @Test
    void testEmptyListSize() {
        List<Integer> emptyList = List.empty();
        assertEquals(0, emptyList.size());
    }

    @Test
    void testListMap() {
        List<Integer> list = List.of(1, 2, 3);
        List<Integer> mappedList = list.map(x -> x * 2);
        assertEquals("Cons(2, Cons(4, Cons(6, Nil)))", mappedList.toString());
    }

    @Test
    void testEmptyListMap() {
        List<Integer> emptyList = List.empty();
        List<Integer> mappedList = emptyList.map(x -> x * 2);
        assertTrue(mappedList.isEmpty());
    }

    @Test
    void testListFilter() {
        List<Integer> list = List.of(1, 2, 3, 4, 5);
        List<Integer> filteredList = list.filter(x -> x % 2 == 0);
        assertEquals("Cons(2, Cons(4, Nil))", filteredList.toString());
    }

    @Test
    void testEmptyListFilter() {
        List<Integer> emptyList = List.empty();
        List<Integer> filteredList = emptyList.filter(x -> x % 2 == 0);
        assertTrue(filteredList.isEmpty());
    }

    @Test
    void testListFoldLeft() {
        List<Integer> list = List.of(1, 2, 3, 4, 5);
        int sum = list.foldLeft(0, acc -> x -> acc + x);
        assertEquals(15, sum);
    }

    @Test
    void testEmptyListFoldLeft() {
        List<Integer> emptyList = List.empty();
        int sum = emptyList.foldLeft(0, acc -> x -> acc + x);
        assertEquals(0, sum);
    }

    @Test
    void testListToString() {
        List<Integer> list = List.of(1, 2, 3);
        assertEquals("Cons(1, Cons(2, Cons(3, Nil)))", list.toString());
    }

    @Test
    void testEmptyListToString() {
        List<Integer> emptyList = List.empty();
        assertEquals("Nil", emptyList.toString());
    }
}