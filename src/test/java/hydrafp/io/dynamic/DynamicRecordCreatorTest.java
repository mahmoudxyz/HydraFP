package hydrafp.io.dynamic;

import hydrafp.io.core.adt.Option;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;

class DynamicRecordTest {

    @Test
    void testCreateDynamicRecord() {
        DynamicRecordCreator.DynamicRecord record = DynamicRecordCreator.createRecord(Map.of(
                "stringValue", "test",
                "intValue", 42,
                "booleanValue", true
        ));

        assertNotNull(record);
        assertEquals("test", record.get("stringValue").getOrElse(null));
        assertEquals(42, record.get("intValue").getOrElse(null));
        assertEquals(true, record.get("booleanValue").getOrElse(null));
    }

    @Test
    void testEquality() {
        DynamicRecordCreator.DynamicRecord record1 = DynamicRecordCreator.createRecord(Map.of(
                "stringValue", "test",
                "intValue", 42,
                "booleanValue", true
        ));

        DynamicRecordCreator.DynamicRecord record2 = DynamicRecordCreator.createRecord(Map.of(
                "stringValue", "test",
                "intValue", 42,
                "booleanValue", true
        ));

        DynamicRecordCreator.DynamicRecord record3 = DynamicRecordCreator.createRecord(Map.of(
                "stringValue", "different",
                "intValue", 24,
                "booleanValue", false
        ));

        assertEquals(record1, record1);
        assertEquals(record1, record2);
        assertNotEquals(record1, record3);
    }

    @Test
    void testToString() {
        DynamicRecordCreator.DynamicRecord record = DynamicRecordCreator.createRecord(Map.of(
                "stringValue", "test",
                "intValue", 42,
                "booleanValue", true
        ));

        String expected = "DynamicRecord{booleanValue=true, intValue=42, stringValue=test}";
        assertEquals(expected, record.toString());
    }

    @Test
    void testUndefinedMethod() {
        DynamicRecordCreator.DynamicRecord record = DynamicRecordCreator.createRecord(Map.of(
                "stringValue", "test",
                "intValue", 42,
                "booleanValue", true
        ));

        assertTrue(record.get("undefinedMethod").isEmpty());
    }

    @Test
    void testNullValues() {
        assertThrows(NullPointerException.class, () ->
                DynamicRecordCreator.createRecord(null)
        );
    }

    @Test
    void testOptionMapping() {
        DynamicRecordCreator.DynamicRecord record = DynamicRecordCreator.createRecord(Map.of(
                "stringValue", "test",
                "intValue", 42
        ));

        Option<Integer> mappedValue = record.get("stringValue")
                .map(obj -> ((String) obj).length());

        assertEquals(4, mappedValue.getOrElse(0));

        Option<Integer> emptyMappedValue = record.get("nonExistent")
                .map(obj -> ((String) obj).length());

        assertTrue(emptyMappedValue.isEmpty());
    }

    @Test
    void testOptionFlatMapping() {
        DynamicRecordCreator.DynamicRecord record = DynamicRecordCreator.createRecord(Map.of(
                "stringValue", "test",
                "intValue", 42
        ));

        Option<Character> mappedValue = record.get("stringValue")
                .flatMap(obj -> {
                    if (obj instanceof String) {
                        String str = (String) obj;
                        return str.isEmpty() ? Option.of(null) : Option.of(str.charAt(0));
                    }
                    return Option.of(null);
                });

        assertEquals('t', mappedValue.getOrElse((Character) null));

        Option<Character> emptyMappedValue = record.get("nonExistent")
                .flatMap(obj -> {
                    if (obj instanceof String) {
                        String str = (String) obj;
                        return str.isEmpty() ? Option.of(null) : Option.of(str.charAt(0));
                    }
                    return Option.of(null);
                });

        assertTrue(emptyMappedValue.isEmpty());
    }
}