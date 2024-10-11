package hydrafp.io.core.adt;

import java.util.function.Function;

public class Pair<A, B> {
    private final A first;
    private final B second;

    private Pair(A first, B second) {
        this.first = first;
        this.second = second;
    }

    public static <A, B> Pair<A, B> of(A first, B second) {
        return new Pair<>(first, second);
    }

    public A first() {
        return first;
    }

    public B second() {
        return second;
    }

    public <C> Pair<C, B> mapFirst(Function<? super A, ? extends C> mapper) {
        return new Pair<>(mapper.apply(first), second);
    }

    public <C> Pair<A, C> mapSecond(Function<? super B, ? extends C> mapper) {
        return new Pair<>(first, mapper.apply(second));
    }

    public <C, D> Pair<C, D> bimap(Function<? super A, ? extends C> firstMapper,
                                   Function<? super B, ? extends D> secondMapper) {
        return new Pair<>(firstMapper.apply(first), secondMapper.apply(second));
    }

    @Override
    public String toString() {
        return "Pair(" + first + ", " + second + ")";
    }
}