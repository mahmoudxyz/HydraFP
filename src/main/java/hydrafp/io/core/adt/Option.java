package hydrafp.io.core.adt;

import java.util.function.Function;

public abstract class Option<T> {

    public static <T> Option<T> of(T value) {
        return value == null ? new None<>() : new Some<>(value);
    }

    public abstract <U> Option<U> flatMap(Function<? super T, Option<U>> mapper);
    public abstract <U> Option<U> map(Function<? super T, ? extends U> mapper);


    public abstract T getOrElse(T other);

    public abstract boolean isEmpty();


    public static final class Some<T> extends Option<T> {
        private final T value;

        public Some(T value) {
            this.value = value;
        }

        @Override
        public T getOrElse(T other) {
            return value;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public <U> Option<U> map(Function<? super T, ? extends U> mapper) {
            return Option.of(mapper.apply(value));
        }
        @Override
        public <U> Option<U> flatMap(Function<? super T, Option<U>> mapper) {
            return mapper.apply(value);
        }

    }

    public static final class None<T> extends Option<T> {
        @Override
        public T getOrElse(T other) {
            return other;
        }

        @Override
        public boolean isEmpty() {
            return true;
        }
        @Override
        public <U> Option<U> map(Function<? super T, ? extends U> mapper) {
            return new None<>();
        }


        @Override
        public <U> Option<U> flatMap(Function<? super T, Option<U>> mapper) {
            return new None<>();
        }
    }



}
