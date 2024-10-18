package hydrafp.io.core.adt;

import hydrafp.io.core.functions.Function1;
import hydrafp.io.core.functions.Function2;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PairTest {

    @Test
    void of_createsPairInstance() {
        Pair<String, Integer> pair = Pair.of("test", 42);
        assertEquals("test", pair.first());
        assertEquals(Integer.valueOf(42), pair.second());
    }

    @Test
    void mapFirst_appliesFunction() {
        Pair<String, Integer> pair = Pair.of("test", 42);
        Either<String, Pair<Integer, Integer>> result = pair.mapFirst(String::length);
        assertTrue(result.isRight());
        assertEquals(Integer.valueOf(4), result.getRight().first());
        assertEquals(Integer.valueOf(42), result.getRight().second());
    }

    @Test
    void mapFirst_handlesNullMapper() {
        Pair<String, Integer> pair = Pair.of("test", 42);
        Either<String, Pair<Object, Integer>> result = pair.mapFirst(null);
        assertTrue(result.isLeft());
        assertEquals("Mapper function cannot be null", result.getLeft());
    }

    @Test
    void mapSecond_appliesFunction() {
        Pair<String, Integer> pair = Pair.of("test", 42);
        Either<String, Pair<String, String>> result = pair.mapSecond(Object::toString);
        assertTrue(result.isRight());
        assertEquals("test", result.getRight().first());
        assertEquals("42", result.getRight().second());
    }

    @Test
    void mapSecond_handlesNullMapper() {
        Pair<String, Integer> pair = Pair.of("test", 42);
        Either<String, Pair<String, Object>> result = pair.mapSecond(null);
        assertTrue(result.isLeft());
        assertEquals("Mapper function cannot be null", result.getLeft());
    }

    @Test
    void bimap_appliesBothFunctions() {
        Pair<String, Integer> pair = Pair.of("test", 42);
        Either<String, Pair<Integer, String>> result = pair.bimap(String::length, Object::toString);
        assertTrue(result.isRight());
        assertEquals(Integer.valueOf(4), result.getRight().first());
        assertEquals("42", result.getRight().second());
    }

    @Test
    void bimap_handlesNullMappers() {
        Pair<String, Integer> pair = Pair.of("test", 42);
        Either<String, Pair<Object, Object>> result1 = pair.bimap(null, Object::toString);
        Either<String, Pair<Object, Object>> result2 = pair.bimap(String::length, null);
        assertTrue(result1.isLeft());
        assertTrue(result2.isLeft());
        assertEquals("Mapper functions cannot be null", result1.getLeft());
        assertEquals("Mapper functions cannot be null", result2.getLeft());
    }

    @Test
    void fold_combinesBothValues() {
        Pair<String, Integer> pair = Pair.of("test", 42);
        Either<String, String> result = pair.fold((s, i) -> s + i);
        assertTrue(result.isRight());
        assertEquals("test42", result.getRight());
    }

    @Test
    void fold_handlesNullMapper() {
        Pair<String, Integer> pair = Pair.of("test", 42);
        Either<String, Object> result = pair.fold(null);
        assertTrue(result.isLeft());
        assertEquals("Mapper function cannot be null", result.getLeft());
    }

    @Test
    void swap_interchangesFirstAndSecond() {
        Pair<String, Integer> pair = Pair.of("test", 42);
        Pair<Integer, String> swapped = pair.swap();
        assertEquals(Integer.valueOf(42), swapped.first());
        assertEquals("test", swapped.second());
    }

    @Test
    void nestFirst_createsNestedPair() {
        Pair<String, Integer> pair = Pair.of("test", 42);
        Pair<Pair<String, Double>, Integer> nested = pair.nestFirst(3.14);
        assertEquals("test", nested.first().first());
        assertEquals(Double.valueOf(3.14), nested.first().second());
        assertEquals(Integer.valueOf(42), nested.second());
    }

    @Test
    void nestSecond_createsNestedPair() {
        Pair<String, Integer> pair = Pair.of("test", 42);
        Pair<String, Pair<Integer, Double>> nested = pair.nestSecond(3.14);
        assertEquals("test", nested.first());
        assertEquals(Integer.valueOf(42), nested.second().first());
        assertEquals(Double.valueOf(3.14), nested.second().second());
    }

    @Test
    void uncurry_convertsToSingleArgumentFunction() {
        Function2<String, Integer, String> biFunction = (s, i) -> s + i;
        Function1<Pair<String, Integer>, Either<String, String>> uncurried = Pair.uncurry(biFunction);
        Either<String, String> result = uncurried.apply(Pair.of("test", 42));
        assertTrue(result.isRight());
        assertEquals("test42", result.getRight());
    }

    @Test
    void uncurry_handlesNullFunction() {
        Function1<Pair<String, Integer>, Either<String, Object>> uncurried = Pair.uncurry(null);
        Either<String, Object> result = uncurried.apply(Pair.of("test", 42));
        assertTrue(result.isLeft());
        assertEquals("Function cannot be null", result.getLeft());
    }

    @Test
    void curry_convertsToBiFunction() {
        Function1<Pair<String, Integer>, String> function = pair -> pair.first() + pair.second();
        Either<String, Function2<String, Integer, String>> curried = Pair.curry(function);
        assertTrue(curried.isRight());
        String result = curried.getRight().apply("test", 42);
        assertEquals("test42", result);
    }

    @Test
    void curry_handlesNullFunction() {
        Either<String, Function2<String, Integer, Object>> curried = Pair.curry(null);
        assertTrue(curried.isLeft());
        assertEquals("Function cannot be null", curried.getLeft());
    }

    @Test
    void equals_returnsTrueForEqualPairs() {
        Pair<String, Integer> pair1 = Pair.of("test", 42);
        Pair<String, Integer> pair2 = Pair.of("test", 42);
        assertEquals(pair1, pair2);
    }

    @Test
    void equals_returnsFalseForDifferentPairs() {
        Pair<String, Integer> pair1 = Pair.of("test", 42);
        Pair<String, Integer> pair2 = Pair.of("test", 43);
        assertNotEquals(pair1, pair2);
    }

    @Test
    void hashCode_returnsSameValueForEqualPairs() {
        Pair<String, Integer> pair1 = Pair.of("test", 42);
        Pair<String, Integer> pair2 = Pair.of("test", 42);
        assertEquals(pair1.hashCode(), pair2.hashCode());
    }

    @Test
    void toString_returnsCorrectRepresentation() {
        Pair<String, Integer> pair = Pair.of("test", 42);
        assertEquals("Pair(test, 42)", pair.toString());
    }
}