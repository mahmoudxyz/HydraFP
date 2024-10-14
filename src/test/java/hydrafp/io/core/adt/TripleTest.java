package hydrafp.io.core.adt;

import org.junit.jupiter.api.Test;

import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

class TripleTest {

    @Test
    void of_createsTripleInstance() {
        Triple<String, Integer, Double> triple = Triple.of("test", 42, 3.14);
        assertEquals("test", triple.first());
        assertEquals(Integer.valueOf(42), triple.second());
        assertEquals(Double.valueOf(3.14), triple.third());
    }

    @Test
    void mapFirst_appliesFunction() {
        Triple<String, Integer, Double> triple = Triple.of("test", 42, 3.14);
        Either<String, Triple<Integer, Integer, Double>> result = triple.mapFirst(String::length);
        assertTrue(result.isRight());
        assertEquals(Integer.valueOf(4), result.getRight().first());
        assertEquals(Integer.valueOf(42), result.getRight().second());
        assertEquals(Double.valueOf(3.14), result.getRight().third());
    }

    @Test
    void mapFirst_handlesNullMapper() {
        Triple<String, Integer, Double> triple = Triple.of("test", 42, 3.14);
        Either<String, Triple<Object, Integer, Double>> result = triple.mapFirst(null);
        assertTrue(result.isLeft());
        assertEquals("Mapper function cannot be null", result.getLeft());
    }

    @Test
    void mapSecond_appliesFunction() {
        Triple<String, Integer, Double> triple = Triple.of("test", 42, 3.14);
        Either<String, Triple<String, String, Double>> result = triple.mapSecond(Object::toString);
        assertTrue(result.isRight());
        assertEquals("test", result.getRight().first());
        assertEquals("42", result.getRight().second());
        assertEquals(Double.valueOf(3.14), result.getRight().third());
    }

    @Test
    void mapSecond_handlesNullMapper() {
        Triple<String, Integer, Double> triple = Triple.of("test", 42, 3.14);
        Either<String, Triple<String, Object, Double>> result = triple.mapSecond(null);
        assertTrue(result.isLeft());
        assertEquals("Mapper function cannot be null", result.getLeft());
    }

    @Test
    void mapThird_appliesFunction() {
        Triple<String, Integer, Double> triple = Triple.of("test", 42, 3.14);
        Either<String, Triple<String, Integer, String>> result = triple.mapThird(Object::toString);
        assertTrue(result.isRight());
        assertEquals("test", result.getRight().first());
        assertEquals(Integer.valueOf(42), result.getRight().second());
        assertEquals("3.14", result.getRight().third());
    }

    @Test
    void mapThird_handlesNullMapper() {
        Triple<String, Integer, Double> triple = Triple.of("test", 42, 3.14);
        Either<String, Triple<String, Integer, Object>> result = triple.mapThird(null);
        assertTrue(result.isLeft());
        assertEquals("Mapper function cannot be null", result.getLeft());
    }

    @Test
    void trimap_appliesAllFunctions() {
        Triple<String, Integer, Double> triple = Triple.of("test", 42, 3.14);
        Either<String, Triple<Integer, String, Integer>> result = triple.trimap(
                String::length,
                Object::toString,
                Double::intValue
        );
        assertTrue(result.isRight());
        assertEquals(Integer.valueOf(4), result.getRight().first());
        assertEquals("42", result.getRight().second());
        assertEquals(Integer.valueOf(3), result.getRight().third());
    }

    @Test
    void trimap_handlesNullMappers() {
        Triple<String, Integer, Double> triple = Triple.of("test", 42, 3.14);
        Either<String, Triple<Object, Object, Object>> result = triple.trimap(null, Object::toString, Double::intValue);
        assertTrue(result.isLeft());
        assertEquals("Mapper functions cannot be null", result.getLeft());
    }

    @Test
    void fold_combinesAllValues() {
        Triple<String, Integer, Double> triple = Triple.of("test", 42, 3.14);
        Either<String, String> result = triple.fold((s, i, d) -> s + i + d);
        assertTrue(result.isRight());
        assertEquals("test423.14", result.getRight());
    }

    @Test
    void fold_handlesNullMapper() {
        Triple<String, Integer, Double> triple = Triple.of("test", 42, 3.14);
        Either<String, Object> result = triple.fold(null);
        assertTrue(result.isLeft());
        assertEquals("Mapper function cannot be null", result.getLeft());
    }

    @Test
    void rotateLeft_rotatesCorrectly() {
        Triple<String, Integer, Double> triple = Triple.of("test", 42, 3.14);
        Triple<Integer, Double, String> rotated = triple.rotateLeft();
        assertEquals(Integer.valueOf(42), rotated.first());
        assertEquals(Double.valueOf(3.14), rotated.second());
        assertEquals("test", rotated.third());
    }

    @Test
    void rotateRight_rotatesCorrectly() {
        Triple<String, Integer, Double> triple = Triple.of("test", 42, 3.14);
        Triple<Double, String, Integer> rotated = triple.rotateRight();
        assertEquals(Double.valueOf(3.14), rotated.first());
        assertEquals("test", rotated.second());
        assertEquals(Integer.valueOf(42), rotated.third());
    }

    @Test
    void toPair_convertsCorrectly() {
        Triple<String, Integer, Double> triple = Triple.of("test", 42, 3.14);
        Pair<String, Pair<Integer, Double>> pair = triple.toPair();
        assertEquals("test", pair.first());
        assertEquals(Integer.valueOf(42), pair.second().first());
        assertEquals(Double.valueOf(3.14), pair.second().second());
    }

    @Test
    void fromPair_convertsCorrectly() {
        Pair<String, Pair<Integer, Double>> pair = Pair.of("test", Pair.of(42, 3.14));
        Triple<String, Integer, Double> triple = Triple.fromPair(pair);
        assertEquals("test", triple.first());
        assertEquals(Integer.valueOf(42), triple.second());
        assertEquals(Double.valueOf(3.14), triple.third());
    }

    @Test
    void uncurry_convertsToSingleArgumentFunction() {
        Triple.Function3<String, Integer, Double, String> triFunction = (s, i, d) -> s + i + d;
        Function<Triple<String, Integer, Double>, Either<String, String>> uncurried = Triple.uncurry(triFunction);
        Either<String, String> result = uncurried.apply(Triple.of("test", 42, 3.14));
        assertTrue(result.isRight());
        assertEquals("test423.14", result.getRight());
    }

    @Test
    void uncurry_handlesNullFunction() {
        Function<Triple<String, Integer, Double>, Either<String, Object>> uncurried = Triple.uncurry(null);
        Either<String, Object> result = uncurried.apply(Triple.of("test", 42, 3.14));
        assertTrue(result.isLeft());
        assertEquals("Function cannot be null", result.getLeft());
    }

    @Test
    void curry_convertsToTriFunction() {
        Function<Triple<String, Integer, Double>, String> function = triple -> triple.first() + triple.second() + triple.third();
        Either<String, Triple.Function3<String, Integer, Double, String>> curried = Triple.curry(function);
        assertTrue(curried.isRight());
        String result = curried.getRight().apply("test", 42, 3.14);
        assertEquals("test423.14", result);
    }

    @Test
    void curry_handlesNullFunction() {
        Either<String, Triple.Function3<String, Integer, Double, Object>> curried = Triple.curry(null);
        assertTrue(curried.isLeft());
        assertEquals("Function cannot be null", curried.getLeft());
    }

    @Test
    void equals_returnsTrueForEqualTriples() {
        Triple<String, Integer, Double> triple1 = Triple.of("test", 42, 3.14);
        Triple<String, Integer, Double> triple2 = Triple.of("test", 42, 3.14);
        assertEquals(triple1, triple2);
    }

    @Test
    void equals_returnsFalseForDifferentTriples() {
        Triple<String, Integer, Double> triple1 = Triple.of("test", 42, 3.14);
        Triple<String, Integer, Double> triple2 = Triple.of("test", 42, 3.15);
        assertNotEquals(triple1, triple2);
    }

    @Test
    void hashCode_returnsSameValueForEqualTriples() {
        Triple<String, Integer, Double> triple1 = Triple.of("test", 42, 3.14);
        Triple<String, Integer, Double> triple2 = Triple.of("test", 42, 3.14);
        assertEquals(triple1.hashCode(), triple2.hashCode());
    }

    @Test
    void toString_returnsCorrectRepresentation() {
        Triple<String, Integer, Double> triple = Triple.of("test", 42, 3.14);
        assertEquals("Triple(test, 42, 3.14)", triple.toString());
    }
}