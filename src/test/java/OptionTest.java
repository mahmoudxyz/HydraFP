import hydrafp.io.core.adt.Option;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class OptionTest {

    @Test
    void shouldReturnSomeWhenValueIsNotNull() {
        Option<Integer> option = Option.of(5);
        assertFalse(option.isEmpty());
        assertEquals(5, option.getOrElse(0));
    }

    @Test
    void shouldReturnNoneWhenValueIsNull() {
        Option<Integer> option = Option.of(null);
        assertTrue(option.isEmpty());
        assertEquals(0, option.getOrElse(0));
    }

    @Test
    void shouldApplyFunctionWhenOptionIsSome() {
        Option<Integer> option = Option.of(5);
        Option<Integer> result = option.map(x -> x * 2);
        assertEquals(10, result.getOrElse(0));
    }

    @Test
    void shouldNotApplyFunctionWhenOptionIsNone() {
        Option<Integer> option = Option.of(null);
        Option<Integer> result = option.map(x -> x * 2);
        assertEquals(0, result.getOrElse(0));
    }

    @Test
    void shouldFlatMapWhenOptionIsSome() {
        Option<Integer> option = Option.of(5);
        Option<Integer> result = option.flatMap(x -> Option.of(x * 2));
        assertEquals(10, result.getOrElse(0));
    }

    @Test
    void shouldNotFlatMapWhenOptionIsNone() {
        Option<Integer> option = Option.of(null);
        Option<Integer> result = option.flatMap(x -> Option.of(10));
        assertEquals(0, result.getOrElse(0));
    }


}
