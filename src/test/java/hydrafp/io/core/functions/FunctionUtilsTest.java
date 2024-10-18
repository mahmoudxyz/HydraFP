package hydrafp.io.core.functions;

import hydrafp.io.core.adt.Pair;
import hydrafp.io.core.adt.Triple;
import hydrafp.io.core.adt.Try;
import hydrafp.io.core.adt.Option;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class FunctionUtilsTest {

    @Test
    void memoize_shouldCacheResult() {
        int[] callCount = {0};
        Function1<Integer, Integer> f = x -> {
            callCount[0]++;
            return x * 2;
        };
        Function1<Integer, Integer> memoized = FunctionUtils.memoize(f);

        assertEquals(6, memoized.apply(3));
        assertEquals(6, memoized.apply(3));
        assertEquals(1, callCount[0]);
    }

    @Test
    void memoize2_shouldCacheResult() {
        int[] callCount = {0};
        Function2<Integer, Integer, Integer> f = (x, y) -> {
            callCount[0]++;
            return x + y;
        };
        Function2<Integer, Integer, Integer> memoized = FunctionUtils.memoize2(f);

        assertEquals(5, memoized.apply(2, 3));
        assertEquals(5, memoized.apply(2, 3));
        assertEquals(1, callCount[0]);
    }

    @Test
    void curry_shouldReturnCurriedFunction() {
        Function2<Integer, Integer, Integer> add = Integer::sum;
        Function1<Integer, Function1<Integer, Integer>> curriedAdd = FunctionUtils.curry(add);

        assertEquals(5, curriedAdd.apply(2).apply(3));
    }

    @Test
    void uncurry_shouldReturnUncurriedFunction() {
        Function1<Integer, Function1<Integer, Integer>> curriedAdd = a -> b -> a + b;
        Function2<Integer, Integer, Integer> add = FunctionUtils.uncurry(curriedAdd);

        assertEquals(5, add.apply(2, 3));
    }

    @Test
    void lift_shouldWrapResultInTry() {
        Function1<Integer, Integer> divide = x -> 10 / x;
        Function1<Integer, Try<Integer>> safeDivide = FunctionUtils.lift(divide);

        assertTrue(safeDivide.apply(2).isSuccess());
        assertTrue(safeDivide.apply(0).isFailure());
    }

    @Test
    void liftOption_shouldWrapResultInOption() {
        Function1<Integer, Integer> maybeDouble = x -> x > 0 ? x * 2 : null;
        Function1<Integer, Option<Integer>> safeMaybeDouble = FunctionUtils.liftOption(maybeDouble);

        assertTrue(safeMaybeDouble.apply(3).isDefined());
        assertTrue(safeMaybeDouble.apply(-1).isEmpty());
    }

    @Test
    void tuple2_shouldConvertFunctionToAcceptPair() {
        Function2<Integer, Integer, Integer> add = Integer::sum;
        Function1<Pair<Integer, Integer>, Integer> tupled = FunctionUtils.tuple2(add);

        assertEquals(5, tupled.apply(Pair.of(2, 3)));
    }

    @Test
    void detuple2_shouldConvertFunctionToAcceptSeparateArguments() {
        Function1<Pair<Integer, Integer>, Integer> tupled = pair -> pair.first() + pair.second();
        Function2<Integer, Integer, Integer> detupled = FunctionUtils.detuple2(tupled);

        assertEquals(5, detupled.apply(2, 3));
    }

    @Test
    void lift2_shouldWrapResultInTry() {
        Function2<Integer, Integer, Integer> divide = (a, b) -> a / b;
        Function2<Integer, Integer, Try<Integer>> safeDivide = FunctionUtils.lift2(divide);

        assertTrue(safeDivide.apply(10, 2).isSuccess());
        assertTrue(safeDivide.apply(10, 0).isFailure());
    }

    @Test
    void liftOption2_shouldWrapResultInOption() {
        Function2<Integer, Integer, Integer> safeDivide = (a, b) -> b != 0 ? a / b : null;
        Function2<Integer, Integer, Option<Integer>> optionDivide = FunctionUtils.liftOption2(safeDivide);

        assertTrue(optionDivide.apply(10, 2).isDefined());
        assertTrue(optionDivide.apply(10, 0).isEmpty());
    }

    @Test
    void tuple3_shouldConvertFunctionToAcceptTriple() {
        Function3<Integer, Integer, Integer, Integer> add3 = (a, b, c) -> a + b + c;
        Function1<Triple<Integer, Integer, Integer>, Integer> tupled = FunctionUtils.tuple3(add3);

        assertEquals(6, tupled.apply(Triple.of(1, 2, 3)));
    }

    @Test
    void detuple3_shouldConvertFunctionToAcceptSeparateArguments() {
        Function1<Triple<Integer, Integer, Integer>, Integer> tupled = triple -> triple.first() + triple.second() + triple.third();
        Function3<Integer, Integer, Integer, Integer> detupled = FunctionUtils.detuple3(tupled);

        assertEquals(6, detupled.apply(1, 2, 3));
    }

    @Test
    void curry3_shouldReturnTriplyCurriedFunction() {
        Function3<Integer, Integer, Integer, Integer> add3 = (a, b, c) -> a + b + c;
        Function1<Integer, Function1<Integer, Function1<Integer, Integer>>> curried = FunctionUtils.curry3(add3);

        assertEquals(6, curried.apply(1).apply(2).apply(3));
    }

    @Test
    void uncurry3_shouldReturnUncurriedFunction() {
        Function1<Integer, Function1<Integer, Function1<Integer, Integer>>> curried = a -> b -> c -> a + b + c;
        Function3<Integer, Integer, Integer, Integer> uncurried = FunctionUtils.uncurry3(curried);

        assertEquals(6, uncurried.apply(1, 2, 3));
    }

    @Test
    void nestedUsage_shouldWorkCorrectly() {
        Function2<Integer, Integer, Integer> add = Integer::sum;

        Function1<Integer, Function1<Integer, Integer>> curriedAdd = FunctionUtils.curry(add);
        Function2<Integer, Integer, Integer> uncurriedMultiply = FunctionUtils.uncurry(a -> b -> a * b);

        Function3<Integer, Integer, Integer, Integer> complex = (a, b, c) -> curriedAdd.apply(uncurriedMultiply.apply(a, b)).apply(c);

        assertEquals(11, complex.apply(2, 3, 5));
    }

    @Test
    void compositionWithLift_shouldHandleExceptions() {
        Function1<Integer, Integer> divideTenBy = x -> 10 / x;
        Function1<Integer, Integer> addOne = x -> x + 1;

        Function1<Integer, Try<Integer>> safeDivide = FunctionUtils.lift(divideTenBy);
        Function1<Integer, Try<Integer>> safeAddOne = FunctionUtils.lift(addOne);

        Function1<Integer, Try<Integer>> composed = x -> safeDivide.apply(x).flatMap(safeAddOne::apply);

        assertTrue(composed.apply(2).isSuccess());
        assertTrue(composed.apply(0).isFailure());
    }

}