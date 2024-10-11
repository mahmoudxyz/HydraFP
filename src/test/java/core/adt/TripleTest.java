package core.adt;

import hydrafp.io.core.adt.Triple;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TripleTest {

    @Test
    void testTripleCreation() {
        Triple<String, Integer, Boolean> triple = Triple.of("Hello", 42, true);
        assertNotNull(triple);
    }

    @Test
    void testTripleFirstValue() {
        Triple<String, Integer, Boolean> triple = Triple.of("Hello", 42, true);
        assertEquals("Hello", triple.first());
    }

    @Test
    void testTripleSecondValue() {
        Triple<String, Integer, Boolean> triple = Triple.of("Hello", 42, true);
        assertEquals(42, triple.second());
    }

    @Test
    void testTripleThirdValue() {
        Triple<String, Integer, Boolean> triple = Triple.of("Hello", 42, true);
        assertTrue(triple.third());
    }

    @Test
    void testTripleMapFirst() {
        Triple<String, Integer, Boolean> triple = Triple.of("Hello", 42, true);
        Triple<Integer, Integer, Boolean> mappedTriple = triple.mapFirst(String::length);
        assertEquals(5, mappedTriple.first());
    }

    @Test
    void testTripleMapFirstDoesNotChangeOthers() {
        Triple<String, Integer, Boolean> triple = Triple.of("Hello", 42, true);
        Triple<Integer, Integer, Boolean> mappedTriple = triple.mapFirst(String::length);
        assertEquals(42, mappedTriple.second());
    }

    @Test
    void testTripleMapSecond() {
        Triple<String, Integer, Boolean> triple = Triple.of("Hello", 42, true);
        Triple<String, String, Boolean> mappedTriple = triple.mapSecond(Object::toString);
        assertEquals("42", mappedTriple.second());
    }

    @Test
    void testTripleMapSecondDoesNotChangeOthers() {
        Triple<String, Integer, Boolean> triple = Triple.of("Hello", 42, true);
        Triple<String, String, Boolean> mappedTriple = triple.mapSecond(Object::toString);
        assertEquals("Hello", mappedTriple.first());
    }

    @Test
    void testTripleMapThird() {
        Triple<String, Integer, Boolean> triple = Triple.of("Hello", 42, true);
        Triple<String, Integer, String> mappedTriple = triple.mapThird(Object::toString);
        assertEquals("true", mappedTriple.third());
    }

    @Test
    void testTripleMapThirdDoesNotChangeOthers() {
        Triple<String, Integer, Boolean> triple = Triple.of("Hello", 42, true);
        Triple<String, Integer, String> mappedTriple = triple.mapThird(Object::toString);
        assertEquals("Hello", mappedTriple.first());
    }

    @Test
    void testTripleTrimapFirst() {
        Triple<String, Integer, Boolean> triple = Triple.of("Hello", 42, true);
        Triple<Integer, Integer, Boolean> trimappedTriple = triple.trimap(String::length, i -> i, b -> b);
        assertEquals(5, trimappedTriple.first());
    }

    @Test
    void testTripleTrimapSecond() {
        Triple<String, Integer, Boolean> triple = Triple.of("Hello", 42, true);
        Triple<String, String, Boolean> trimappedTriple = triple.trimap(s -> s, Object::toString, b -> b);
        assertEquals("42", trimappedTriple.second());
    }

    @Test
    void testTripleTrimapThird() {
        Triple<String, Integer, Boolean> triple = Triple.of("Hello", 42, true);
        Triple<String, Integer, String> trimappedTriple = triple.trimap(s -> s, i -> i, Object::toString);
        assertEquals("true", trimappedTriple.third());
    }

    @Test
    void testTripleToString() {
        Triple<String, Integer, Boolean> triple = Triple.of("Hello", 42, true);
        assertEquals("Triple(Hello, 42, true)", triple.toString());
    }
}