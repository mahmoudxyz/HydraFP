package hydrafp.io.core.memo;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class Memoize {
    private Memoize() {}

    public static <T, R> Function<T, R> memoize(Function<T, R> function) {
        Map<T, R> cache = new ConcurrentHashMap<>();
        return input -> cache.computeIfAbsent(input, function);
    }

    public static <T, U, R> BiFunction<T, U, R> memoize(BiFunction<T, U, R> function) {
        Map<Pair<T, U>, R> cache = new ConcurrentHashMap<>();
        return (t, u) -> cache.computeIfAbsent(Pair.of(t, u), pair -> function.apply(pair.first(), pair.second()));
    }

    @FunctionalInterface
    public interface BiFunction<T, U, R> {
        R apply(T t, U u);
    }

    private static class Pair<T, U> {
        private final T first;
        private final U second;

        private Pair(T first, U second) {
            this.first = first;
            this.second = second;
        }

        public static <T, U> Pair<T, U> of(T first, U second) {
            return new Pair<>(first, second);
        }

        public T first() { return first; }
        public U second() { return second; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Pair<?, ?> pair = (Pair<?, ?>) o;
            return first.equals(pair.first) && second.equals(pair.second);
        }

        @Override
        public int hashCode() {
            return 31 * first.hashCode() + second.hashCode();
        }
    }
}