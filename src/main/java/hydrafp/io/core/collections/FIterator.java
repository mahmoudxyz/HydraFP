package hydrafp.io.core.collections;

import hydrafp.io.core.adt.Option;

public interface FIterator<T> {
    boolean hasNext();
    Option<T> next();
}