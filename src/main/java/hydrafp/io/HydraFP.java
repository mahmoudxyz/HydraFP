package hydrafp.io;

public class HydraFP {

    public static <T1, T2, R> Function1<T1, Function1<T2, R>> curry(Function2<T1, T2, R> function) {
        return t1 -> t2 -> function.apply(t1, t2);
    }

    public static <T1, T2, T3, R> Function1<T3, R> partial(Function3<T1, T2, T3, R> function, T1 t1, T2 t2) {
        return t3 -> function.apply(t1, t2, t3);
    }

    public static <T, R> Function1<T, R> compose(Function1<T, R> f, Function1<R, R>... functions) {
        Function1<T, R> result = f;
        for (Function1<R, R> function : functions) {
            result = result.andThen(function);
        }
        return result;
    }

    public static <T> Function1<T, T> identity() {
        return t -> t;
    }

    public static <T, R> Function1<T, R> constant(R value) {
        return t -> value;
    }

    @FunctionalInterface
    public interface Function1<T, R> {
        R apply(T t);

        default <V> Function1<T, V> andThen(Function1<? super R, ? extends V> after) {
            return (T t) -> after.apply(apply(t));
        }
    }

    @FunctionalInterface
    public interface Function2<T1, T2, R> {
        R apply(T1 t1, T2 t2);
    }

    @FunctionalInterface
    public interface Function3<T1, T2, T3, R> {
        R apply(T1 t1, T2 t2, T3 t3);
    }
}