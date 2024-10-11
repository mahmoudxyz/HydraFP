package hydrafp.io.core.validation;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class Validation<E, T> {
    private final List<E> errors;
    private final T value;

    private Validation(List<E> errors, T value) {
        this.errors = errors;
        this.value = value;
    }

    public static <E, T> Validation<E, T> valid(T value) {
        return new Validation<>(new ArrayList<>(), value);
    }

    public static <E, T> Validation<E, T> invalid(E error) {
        List<E> errors = new ArrayList<>();
        errors.add(error);
        return new Validation<>(errors, null);
    }

    public static <E, T> Validation<E, T> invalid(List<E> errors) {
        return new Validation<>(new ArrayList<>(errors), null);
    }

    public boolean isValid() {
        return errors.isEmpty();
    }

    public List<E> getErrors() {
        return new ArrayList<>(errors);
    }

    public T getValue() {
        if (!isValid()) {
            throw new IllegalStateException("Cannot get value from invalid Validation");
        }
        return value;
    }

    public <U> Validation<E, U> map(Function<? super T, ? extends U> mapper) {
        if (isValid()) {
            return valid(mapper.apply(value));
        }
        return new Validation<>(errors, null);
    }

    public <U> Validation<E, U> flatMap(Function<? super T, Validation<E, U>> mapper) {
        if (isValid()) {
            return mapper.apply(value);
        }
        return new Validation<>(errors, null);
    }

    public static <E, T, U, V> Validation<E, V> map2(
            Validation<E, T> v1,
            Validation<E, U> v2,
            Function<T, Function<U, V>> f
    ) {
        if (v1.isValid() && v2.isValid()) {
            return valid(f.apply(v1.getValue()).apply(v2.getValue()));
        }
        List<E> errors = new ArrayList<>(v1.getErrors());
        errors.addAll(v2.getErrors());
        return invalid(errors);
    }
}