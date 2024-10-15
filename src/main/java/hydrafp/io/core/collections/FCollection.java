package hydrafp.io.core.collections;

import hydrafp.io.core.adt.Option;


import java.util.function.Function;
import java.util.function.Predicate;

public interface FCollection<T> extends FIterable<T> {
    int size();
    boolean isEmpty();
    boolean contains(T element);
    FCollection<T> add(T element);
    FCollection<T> remove(T element);
    <U> FCollection<U> map(Function<? super T, ? extends U> mapper);
    FCollection<T> filter(Predicate<? super T> predicate);
    <U> FCollection<U> flatMap(Function<? super T, ? extends FIterable<? extends U>> mapper);
    Option<T> find(Predicate<? super T> predicate);
    String toReadableString();
}