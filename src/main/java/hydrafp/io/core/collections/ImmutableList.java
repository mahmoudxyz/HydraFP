package hydrafp.io.core.collections;

import hydrafp.io.core.adt.Either;
import hydrafp.io.core.adt.Option;
import hydrafp.io.core.adt.Try;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.Collector;

public abstract class ImmutableList<T> implements FCollection<T> {

    public static <T> ImmutableList<T> empty() {
        return Nil.instance();
    }

    @SafeVarargs
    public static <T> ImmutableList<T> of(T... elements) {
        ImmutableList<T> result = empty();
        for (int i = elements.length - 1; i >= 0; i--) {
            result = result.cons(elements[i]);
        }
        return result;
    }

    public abstract ImmutableList<T> cons(T element);
    public abstract Option<T> head();
    public abstract ImmutableList<T> tail();


    @Override
    public abstract boolean isEmpty();

    @Override
    public abstract int size();

    @Override
    public boolean contains(T element) {
        return find(t -> Objects.equals(t, element)).isDefined();
    }

    @Override
    public ImmutableList<T> add(T element) {
        return cons(element);
    }

    @Override
    public ImmutableList<T> remove(T element) {
        return filter(t -> !Objects.equals(t, element));
    }

    @Override
    public <U> ImmutableList<U> map(Function<? super T, ? extends U> mapper) {
        return foldRight(empty(), (t, acc) -> acc.cons(mapper.apply(t)));
    }

    @Override
    public ImmutableList<T> filter(Predicate<? super T> predicate) {
        return foldRight(empty(), (t, acc) -> predicate.test(t) ? acc.cons(t) : acc);
    }

    @Override
    public <U> ImmutableList<U> flatMap(Function<? super T, ? extends FIterable<? extends U>> mapper) {
        return foldRight(empty(), (t, acc) -> {
            FIterable<? extends U> mapped = mapper.apply(t);
            ImmutableList<U> mappedList = ImmutableList.fromFIterable(mapped);
            return mappedList.appendAll(acc);
        });
    }

    @Override
    public Option<T> find(Predicate<? super T> predicate) {
        return filter(predicate).head();
    }


    @Override
    public String toReadableString() {
        return asIterator()
                .map(Object::toString)
                .collect(java.util.stream.Collectors.joining(", ", "[", "]"));
    }

    public ImmutableList<T> appendAll(ImmutableList<T> other) {
        return foldRight(other, (t, acc) -> acc.cons(t));
    }

    public <U> U foldLeft(U initial, BiFunction<U, T, U> operator) {
        U result = initial;
        ImmutableList<T> current = this;
        while (!current.isEmpty()) {
            result = operator.apply(result, current.head().getOrElse(() -> null));
            current = current.tail();
        }
        return result;
    }

    public <U> U foldRight(U initial, BiFunction<T, U, U> operator) {
        return reverse().foldLeft(initial, (acc, t) -> operator.apply(t, acc));
    }

    public ImmutableList<T> reverse() {
        return foldLeft(empty(), ImmutableList::cons);
    }

    public ImmutableList<T> take(int n) {
        if (n <= 0 || isEmpty()) return empty();
        return tail().take(n - 1).cons(head().getOrElse(() -> null));
    }

    public ImmutableList<T> drop(int n) {
        if (n <= 0 || isEmpty()) return this;
        return tail().drop(n - 1);
    }

    public Either<Throwable, T> get(int index) {
        if (index < 0) {
            return Either.left(new IndexOutOfBoundsException("Index out of bounds: " + index));
        }
        ImmutableList<T> current = this;
        for (int i = 0; i < index; i++) {
            if (current.isEmpty()) {
                return Either.left(new IndexOutOfBoundsException("Index out of bounds: " + index));
            }
            current = current.tail();
        }
        return current.head().toEither(() -> new NoSuchElementException("Index out of bounds: " + index));
    }

    @Override
    public FIterator<T> iterator() {
        return new FIterator<T>() {
            private ImmutableList<T> current = ImmutableList.this;

            @Override
            public boolean hasNext() {
                return !current.isEmpty();
            }

            @Override
            public Option<T> next() {
                if (!hasNext()) {
                    return Option.none();
                }
                Option<T> value = current.head();
                current = current.tail();
                return value;
            }
        };
    }

    @Override
    public Stream<T> asIterator() {
        return FCollection.super.asIterator();
    }



    public static <T> ImmutableList<T> fromFIterable(FIterable<? extends T> iterable) {
        final ImmutableList<T>[] result = new ImmutableList[]{empty()};
        for (FIterator<? extends T> it = iterable.iterator(); it.hasNext(); ) {
            Option<? extends T> element = it.next();
            element.forEach(e -> result[0] = result[0].cons(e));
        }
        return result[0].reverse();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof ImmutableList<?> other)) return false;
        if (this.size() != other.size()) return false;
        FIterator<T> it1 = this.iterator();
        FIterator<?> it2 = other.iterator();
        while (it1.hasNext() && it2.hasNext()) {
            if (!Objects.equals(it1.next().getOrElse(() -> null), it2.next().getOrElse(() -> null))) return false;
        }
        return !it1.hasNext() && !it2.hasNext();
    }

    @Override
    public int hashCode() {
        int result = 1;
        for (FIterator<T> it = iterator(); it.hasNext(); ) {
            T element = it.next().getOrElse(() -> null);
            result = 31 * result + (element == null ? 0 : element.hashCode());
        }
        return result;
    }



    public <U, R> ImmutableList<R> zip(ImmutableList<U> other, BiFunction<T, U, R> zipper) {
        if (isEmpty() || other.isEmpty()) return empty();
        return tail().zip(other.tail(), zipper)
                .cons(zipper.apply(head().get(), other.head().get()));
    }

    public <R> ImmutableList<R> collect(Function<T, Option<R>> f) {
        return flatMap(t -> f.apply(t).map(ImmutableList::of).getOrElse(ImmutableList::empty));
    }

    public Try<T> getAsTry(int index) {
        return Try.fromEither(get(index));
    }

    public Option<T> getAsOption(int i) {
        return Option.fromEither(get(i));
    }


    private static final class Nil<T> extends ImmutableList<T> {
        private static final Nil<?> INSTANCE = new Nil<>();

        @SuppressWarnings("unchecked")
        public static <T> Nil<T> instance() {
            return (Nil<T>) INSTANCE;
        }

        @Override
        public ImmutableList<T> cons(T element) {
            return new Cons<>(element, this);
        }

        @Override
        public Option<T> head() {
            return Option.none();
        }

        @Override
        public ImmutableList<T> tail() {
            return this;
        }

        @Override
        public boolean isEmpty() {
            return true;
        }

        @Override
        public int size() {
            return 0;
        }

        @Override
        public String toString() {
            return "Nil";
        }
    }

    private static final class Cons<T> extends ImmutableList<T> {
        private final T head;
        private final ImmutableList<T> tail;
        private final int size;

        private Cons(T head, ImmutableList<T> tail) {
            this.head = head;
            this.tail = tail;
            this.size = 1 + tail.size();
        }

        @Override
        public ImmutableList<T> cons(T element) {
            return new Cons<>(element, this);
        }

        @Override
        public Option<T> head() {
            return Option.some(head);
        }

        @Override
        public ImmutableList<T> tail() {
            return tail;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public int size() {
            return size;
        }
        @Override
        public String toString() {
            return "Cons(" + head + ", " + tail + ")";
        }
    }
}