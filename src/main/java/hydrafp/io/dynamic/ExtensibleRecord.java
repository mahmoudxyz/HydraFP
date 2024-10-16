package hydrafp.io.dynamic;

import hydrafp.io.core.adt.Option;
import hydrafp.io.core.lens.Lens;
import hydrafp.io.core.validation.Validation;

import java.util.*;
import java.util.function.Function;

public class ExtensibleRecord {
    private final Map<String, Object> values;

    private ExtensibleRecord(Map<String, Object> values) {
        this.values = new HashMap<>(values);
    }

    public static ExtensibleRecord create() {
        return new ExtensibleRecord(new HashMap<>());
    }

    public static ExtensibleRecord of(Map<String, Object> values) {
        return new ExtensibleRecord(values);
    }

    public <T> Option<T> get(String key) {
        return Option.of((T) values.get(key));
    }

    public <T> Option<T> get(Field<T> field) {
        return Option.of((T) values.get(field.getName()));
    }

    public ExtensibleRecord extend(String key, Object value) {
        Map<String, Object> newValues = new HashMap<>(this.values);
        newValues.put(key, value);
        return new ExtensibleRecord(newValues);
    }

    public <T> ExtensibleRecord extend(Field<T> field, T value) {
        return extend(field.getName(), value);
    }

    public <T> ExtensibleRecord modify(String key, Function<T, ?> modifier) {
        return get(key).<ExtensibleRecord>map(value -> {
            Map<String, Object> newValues = new HashMap<>(this.values);
            newValues.put(key, modifier.apply((T) value));
            return new ExtensibleRecord(newValues);
        }).getOrElse(this);
    }

    public <T> ExtensibleRecord modify(Field<T> field, Function<T, T> modifier) {
        return modify(field.getName(), modifier);
    }

    public <R> Option<R> match(Pattern<ExtensibleRecord, R> pattern) {
        return pattern.match(this);
    }

    public static <T> Lens<ExtensibleRecord, T> lens(Field<T> field) {
        return Lens.of(
                record -> record.get(field).getOrElse((T) null),
                record -> value -> record.extend(field, value)
        );
    }

    @Override
    public String toString() {
        return "ExtensibleRecord" + values;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof ExtensibleRecord)) return false;
        ExtensibleRecord other = (ExtensibleRecord) obj;
        return this.values.equals(other.values);
    }

    @Override
    public int hashCode() {
        return values.hashCode();
    }

    public static class Field<T> {
        private final String name;

        private Field(String name) {
            this.name = name;
        }

        public static <T> Field<T> of(String name) {
            return new Field<>(name);
        }

        public String getName() {
            return name;
        }
    }

    public interface Pattern<T, R> {
        Option<R> match(T value);
    }

    public static <R> Pattern<ExtensibleRecord, R> pattern(Function<ExtensibleRecord, Option<R>> matcher) {
        return matcher::apply;
    }

    public static class RecordValidation<E> {
        private final List<Function<ExtensibleRecord, Validation<E, ExtensibleRecord>>> validators;

        private RecordValidation(List<Function<ExtensibleRecord, Validation<E, ExtensibleRecord>>> validators) {
            this.validators = validators;
        }

        @SafeVarargs
        public static <E> RecordValidation<E> of(Function<ExtensibleRecord, Validation<E, ExtensibleRecord>>... validators) {
            return new RecordValidation<>(List.of(validators));
        }

        public Validation<E, Object> validate(ExtensibleRecord record) {
            List<E> allErrors = new ArrayList<>();

            for (Function<ExtensibleRecord, Validation<E, ExtensibleRecord>> validator : validators) {
                Validation<E, ExtensibleRecord> result = validator.apply(record);
                if (!result.isValid()) {
                    allErrors.addAll(result.getErrors());
                }
            }

            if (allErrors.isEmpty()) {
                return Validation.valid(record);
            } else {
                return Validation.invalid(allErrors);
            }
        }
    }

}