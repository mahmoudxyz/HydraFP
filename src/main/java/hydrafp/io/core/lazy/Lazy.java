package hydrafp.io.core.lazy;

import java.util.function.Supplier;

public class Lazy<T> {
    private Supplier<T> supplier;
    private T value;
    private boolean evaluated = false;

    private Lazy(Supplier<T> supplier) {
        this.supplier = supplier;
    }

    public static <T> Lazy<T> of(Supplier<T> supplier) {
        return new Lazy<>(supplier);
    }

    public T get() {
        if (!evaluated) {
            value = supplier.get();
            evaluated = true;
            supplier = null; // Allow GC to collect the supplier
        }
        return value;
    }

    public boolean isEvaluated() {
        return evaluated;
    }

    public <U> Lazy<U> map(java.util.function.Function<? super T, ? extends U> mapper) {
        return Lazy.of(() -> mapper.apply(get()));
    }

    public <U> Lazy<U> flatMap(java.util.function.Function<? super T, Lazy<U>> mapper) {
        return Lazy.of(() -> mapper.apply(get()).get());
    }
}