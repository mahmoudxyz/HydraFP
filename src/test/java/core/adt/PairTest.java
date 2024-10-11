package core.adt;

import hydrafp.io.core.adt.Pair;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PairTest {

    @Test
    void testPairCreation() {
        Pair<String, Integer> pair = Pair.of("Hello", 42);
        assertNotNull(pair);
    }

    @Test
    void testPairFirstValue() {
        Pair<String, Integer> pair = Pair.of("Hello", 42);
        assertEquals("Hello", pair.first());
    }

    @Test
    void testPairSecondValue() {
        Pair<String, Integer> pair = Pair.of("Hello", 42);
        assertEquals(42, pair.second());
    }

    @Test
    void testPairMapFirst() {
        Pair<String, Integer> pair = Pair.of("Hello", 42);
        Pair<Integer, Integer> mappedPair = pair.mapFirst(String::length);
        assertEquals(5, mappedPair.first());
    }

    @Test
    void testPairMapFirstDoesNotChangeSecond() {
        Pair<String, Integer> pair = Pair.of("Hello", 42);
        Pair<Integer, Integer> mappedPair = pair.mapFirst(String::length);
        assertEquals(42, mappedPair.second());
    }

    @Test
    void testPairMapSecond() {
        Pair<String, Integer> pair = Pair.of("Hello", 42);
        Pair<String, String> mappedPair = pair.mapSecond(Object::toString);
        assertEquals("42", mappedPair.second());
    }

    @Test
    void testPairMapSecondDoesNotChangeFirst() {
        Pair<String, Integer> pair = Pair.of("Hello", 42);
        Pair<String, String> mappedPair = pair.mapSecond(Object::toString);
        assertEquals("Hello", mappedPair.first());
    }

    @Test
    void testPairBimapFirst() {
        Pair<String, Integer> pair = Pair.of("Hello", 42);
        Pair<Integer, Integer> bimappedPair = pair.bimap(String::length, i -> i);
        assertEquals(5, bimappedPair.first());
    }

    @Test
    void testPairBimapSecond() {
        Pair<String, Integer> pair = Pair.of("Hello", 42);
        Pair<String, String> bimappedPair = pair.bimap(s -> s, Object::toString);
        assertEquals("42", bimappedPair.second());
    }

    @Test
    void testPairToString() {
        Pair<String, Integer> pair = Pair.of("Hello", 42);
        assertEquals("Pair(Hello, 42)", pair.toString());
    }
}