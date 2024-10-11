package hydrafp.io.core.validation;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;

class ValidationTest {

    @Test
    void testValidCreation() {
        Validation<String, Integer> validation = Validation.valid(5);
        assertTrue(validation.isValid());
        assertEquals(5, validation.getValue());
        assertTrue(validation.getErrors().isEmpty());
    }

    @Test
    void testInvalidCreation() {
        Validation<String, Integer> validation = Validation.invalid("Error");
        assertFalse(validation.isValid());
        assertThrows(IllegalStateException.class, validation::getValue);
        assertEquals(1, validation.getErrors().size());
        assertEquals("Error", validation.getErrors().get(0));
    }

    @Test
    void testMap() {
        Validation<String, Integer> valid = Validation.valid(5);
        Validation<String, String> mapped = valid.map(Object::toString);
        assertTrue(mapped.isValid());
        assertEquals("5", mapped.getValue());

        Validation<String, Integer> invalid = Validation.invalid("Error");
        Validation<String, String> mappedInvalid = invalid.map(Object::toString);
        assertFalse(mappedInvalid.isValid());
        assertEquals(1, mappedInvalid.getErrors().size());
        assertEquals("Error", mappedInvalid.getErrors().get(0));
    }

    @Test
    void testFlatMap() {
        Validation<String, Integer> valid = Validation.valid(5);
        Validation<String, String> flatMapped = valid.flatMap(i -> Validation.valid(i.toString()));
        assertTrue(flatMapped.isValid());
        assertEquals("5", flatMapped.getValue());

        Validation<String, Integer> invalid = Validation.invalid("Error");
        Validation<String, String> flatMappedInvalid = invalid.flatMap(i -> Validation.valid(i.toString()));
        assertFalse(flatMappedInvalid.isValid());
        assertEquals(1, flatMappedInvalid.getErrors().size());
        assertEquals("Error", flatMappedInvalid.getErrors().get(0));
    }

    @Test
    void testMap2() {
        Validation<String, Integer> v1 = Validation.valid(5);
        Validation<String, Integer> v2 = Validation.valid(3);
        Validation<String, Integer> result = Validation.map2(v1, v2, i -> j -> i + j);
        assertTrue(result.isValid());
        assertEquals(8, result.getValue());

        Validation<String, Integer> invalid1 = Validation.invalid("Error 1");
        Validation<String, Integer> invalid2 = Validation.invalid("Error 2");
        Validation<String, Integer> invalidResult = Validation.map2(invalid1, invalid2, i -> j -> i + j);
        assertFalse(invalidResult.isValid());
        assertEquals(2, invalidResult.getErrors().size());
        assertTrue(invalidResult.getErrors().containsAll(Arrays.asList("Error 1", "Error 2")));
    }
}