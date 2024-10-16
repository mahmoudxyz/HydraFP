package hydrafp.io.dynamic;


import hydrafp.io.core.adt.Option;
import hydrafp.io.core.lens.Lens;
import hydrafp.io.core.validation.Validation;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ExtensibleRecordTest {

    private static final ExtensibleRecord.Field<String> NAME = ExtensibleRecord.Field.of("name");
    private static final ExtensibleRecord.Field<Integer> AGE = ExtensibleRecord.Field.of("age");
    private static final ExtensibleRecord.Field<ExtensibleRecord> ADDRESS = ExtensibleRecord.Field.of("address");
    private static final ExtensibleRecord.Field<String> CITY = ExtensibleRecord.Field.of("city");

    @Test
    void testBasicOperations() {
        ExtensibleRecord record = ExtensibleRecord.create()
                .extend(NAME, "Alice")
                .extend(AGE, 30);

        assertEquals("Alice", record.get(NAME).getOrElse(""));
        assertEquals(30, record.get(AGE).getOrElse(0));
        assertTrue(record.get("nonexistent").isEmpty());
    }

    @Test
    void testModify() {
        ExtensibleRecord record = ExtensibleRecord.create()
                .extend(NAME, "Bob")
                .extend(AGE, 25);

        ExtensibleRecord modified = record.modify(AGE, age -> age + 1);

        assertEquals("Bob", modified.get(NAME).getOrElse((String) null));
        assertEquals(26, modified.get(AGE).getOrElse((Integer) null));
    }

    @Test
    void testEquality() {
        ExtensibleRecord record1 = ExtensibleRecord.create()
                .extend(NAME, "Charlie")
                .extend(AGE, 35);

        ExtensibleRecord record2 = ExtensibleRecord.create()
                .extend(AGE, 35)
                .extend(NAME, "Charlie");

        ExtensibleRecord record3 = ExtensibleRecord.create()
                .extend(NAME, "David")
                .extend(AGE, 40);

        assertEquals(record1, record2);
        assertNotEquals(record1, record3);
    }

    @Test
    void testLenses() {
        Lens<ExtensibleRecord, String> cityLens = ExtensibleRecord.lens(ADDRESS).compose(ExtensibleRecord.lens(CITY));

        ExtensibleRecord record = ExtensibleRecord.create()
                .extend(NAME, "Eve")
                .extend(ADDRESS, ExtensibleRecord.create().extend(CITY, "New York"));

        assertEquals("New York", cityLens.get(record));

        ExtensibleRecord updated = cityLens.set(record, "Los Angeles");
        assertEquals("Los Angeles", cityLens.get(updated));
        assertEquals("Eve", updated.get(NAME).getOrElse((String) null));
    }

    @Test
    void testPatternMatching() {
        ExtensibleRecord.Pattern<ExtensibleRecord, String> personPattern = ExtensibleRecord.pattern(r ->
                r.get(NAME).flatMap(name ->
                        r.get(AGE).map(age ->
                                name + " is " + age + " years old"
                        )
                )
        );

        ExtensibleRecord record = ExtensibleRecord.create()
                .extend(NAME, "Frank")
                .extend(AGE, 45);

        Option<String> result = record.match(personPattern);
        assertEquals("Frank is 45 years old", result.getOrElse((String) null));

        ExtensibleRecord incompleteRecord = ExtensibleRecord.create()
                .extend(NAME, "Grace");

        assertTrue(incompleteRecord.match(personPattern).isEmpty());
    }

    @Test
    void testValidation() {
        ExtensibleRecord.RecordValidation<String> personValidation = ExtensibleRecord.RecordValidation.of(
                r -> r.get(NAME).map(String::length)
                        .map(length -> length > 0 ? Validation.<String, ExtensibleRecord>valid(r) : Validation.<String, ExtensibleRecord>invalid("Name cannot be empty"))
                        .getOrElse(() -> Validation.invalid("Name is required")),
                r -> r.get(AGE)
                        .map(age -> age >= 0 ? Validation.<String, ExtensibleRecord>valid(r) : Validation.<String, ExtensibleRecord>invalid("Age cannot be negative"))
                        .getOrElse(() -> Validation.invalid("Age is required"))
        );

        ExtensibleRecord validRecord = ExtensibleRecord.create()
                .extend(NAME, "Helen")
                .extend(AGE, 50);

        Validation<String, Object> validResult = personValidation.validate(validRecord);
        assertTrue(validResult.isValid());
        assertTrue(validResult.getErrors().isEmpty());

        ExtensibleRecord invalidRecord = ExtensibleRecord.create()
                .extend(NAME, "")
                .extend(AGE, -5);

        Validation<String, Object> invalidResult = personValidation.validate(invalidRecord);
        assertFalse(invalidResult.isValid());
        List<String> errors = invalidResult.getErrors();
        assertEquals(2, errors.size());
        System.out.println(errors);
        assertTrue(errors.contains("Name cannot be empty"));
        assertTrue(errors.contains("Age cannot be negative"));
    }


}