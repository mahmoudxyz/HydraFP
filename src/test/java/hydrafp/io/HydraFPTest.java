package hydrafp.io;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class HydraFPTest {

    @Test
    void testCurry() {
        HydraFP.Function2<Integer, Integer, Integer> add = (a, b) -> a + b;
        HydraFP.Function1<Integer, HydraFP.Function1<Integer, Integer>> curriedAdd = HydraFP.curry(add);

        assertEquals(5, curriedAdd.apply(2).apply(3));
        assertEquals(5, curriedAdd.apply(2).apply(3)); // Test multiple invocations
    }

    @Test
    void testPartial() {
        HydraFP.Function3<Integer, Integer, Integer, Integer> add3 = (a, b, c) -> a + b + c;
        HydraFP.Function1<Integer, Integer> add5 = HydraFP.partial(add3, 2, 3);

        assertEquals(10, add5.apply(5));
        assertEquals(11, add5.apply(6)); // Test with different argument
    }

    @Test
    void testCompose() {
        HydraFP.Function1<Integer, Integer> add1 = x -> x + 1;
        HydraFP.Function1<Integer, Integer> multiply2 = x -> x * 2;
        HydraFP.Function1<Integer, Integer> subtract3 = x -> x - 3;

        HydraFP.Function1<Integer, Integer> composed = HydraFP.compose(add1, multiply2, subtract3);

        assertEquals(5, composed.apply(3)); // ((3 + 1) * 2) - 3 = 5
    }

    @Test
    void testIdentity() {
        HydraFP.Function1<String, String> identity = HydraFP.identity();

        assertEquals("test", identity.apply("test"));
        assertSame("test", identity.apply("test")); // Should return the same object
    }

    @Test
    void testConstant() {
        String constantValue = "constant";
        HydraFP.Function1<Integer, String> constant = HydraFP.constant(constantValue);

        assertEquals(constantValue, constant.apply(1));
        assertEquals(constantValue, constant.apply(100)); // Should always return the same value
        assertSame(constantValue, constant.apply(1000)); // Should return the same object
    }

    @Test
    void testCombinedFunctions() {
        HydraFP.Function2<Integer, Integer, Integer> multiply = (a, b) -> a * b;
        HydraFP.Function1<Integer, HydraFP.Function1<Integer, Integer>> curriedMultiply = HydraFP.curry(multiply);

        HydraFP.Function3<Integer, Integer, Integer, Integer> add3 = (a, b, c) -> a + b + c;
        HydraFP.Function1<Integer, Integer> add5 = HydraFP.partial(add3, 2, 3);

        HydraFP.Function1<Integer, Integer> complexFunction = HydraFP.compose(
                curriedMultiply.apply(2),
                add5,
                HydraFP.identity(),
                x -> x * x
        );

        // ((3 * 2) + 5) ^ 2 = 11^2 = 121
        assertEquals(121, complexFunction.apply(3));
    }

    @Test
    public void testPartialApplication() {
        // Arrange: Create a function that takes three arguments
        HydraFP.Function3<Integer, Integer, Integer, Integer> addThree = (a, b, c) -> a + b + c;

        // Act: Partially apply the first two arguments
        HydraFP.Function1<Integer, Integer> addFive = HydraFP.partial(addThree, 2, 3);

        // Apply the partially applied function
        Integer result = addFive.apply(5);

        // Assert: Check if the result is as expected
        assertEquals(10, result);
    }

    @Test
    public void testCurryingOfTwoArguments() {
        // Arrange: Create a two-argument function
        HydraFP.Function2<Integer, Integer, Integer> add = Integer::sum;

        // Act: Curry the function
        HydraFP.Function1<Integer, HydraFP.Function1<Integer, Integer>> curriedAdd = HydraFP.curry(add);

        // Apply the curried function
        HydraFP.Function1<Integer, Integer> add5 = curriedAdd.apply(5);
        Integer result = add5.apply(10);

        // Assert: Check if the result is as expected
        assertEquals(15, result);
    }
}