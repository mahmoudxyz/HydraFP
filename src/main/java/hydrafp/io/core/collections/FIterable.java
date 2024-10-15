package hydrafp.io.core.collections;

import hydrafp.io.core.adt.Option;

import java.util.Iterator;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public interface FIterable<T> {
     FIterator<T> iterator();

    default Iterable<T> asIterable() {
        return () -> new Iterator<T>() {
            private final FIterator<T> fIterator = FIterable.this.iterator();

            @Override
            public boolean hasNext() {
                return fIterator.hasNext();
            }

            @Override
            public T next() {
                return fIterator.next().getOrElseThrow(() -> new java.util.NoSuchElementException("No more elements"));
            }
        };
    }

    default Stream<T> stream() {
        return StreamSupport.stream(asIterable().spliterator(), false);
    }

    default Stream<T> asIterator() {
        return Stream.generate(new java.util.function.Supplier<Option<T>>() {
            private final FIterator<T> it = iterator();
            @Override
            public Option<T> get() {
                return it.hasNext() ? it.next() : Option.none();
            }
        }).takeWhile(Option::isDefined).flatMap(opt -> opt.map(Stream::of).getOrElse(Stream::empty));
    }
}

