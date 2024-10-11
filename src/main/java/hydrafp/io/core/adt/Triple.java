package hydrafp.io.core.adt;

import java.util.function.Function;

public class Triple<A, B, C> {
    private final A first;
    private final B second;
    private final C third;

    private Triple(A first, B second, C third) {
        this.first = first;
        this.second = second;
        this.third = third;
    }

    public static <A, B, C> Triple<A, B, C> of(A first, B second, C third) {
        return new Triple<>(first, second, third);
    }

    public A first() {
        return first;
    }

    public B second() {
        return second;
    }

    public C third() {
        return third;
    }

    public <D> Triple<D, B, C> mapFirst(Function<? super A, ? extends D> mapper) {
        return new Triple<>(mapper.apply(first), second, third);
    }

    public <D> Triple<A, D, C> mapSecond(Function<? super B, ? extends D> mapper) {
        return new Triple<>(first, mapper.apply(second), third);
    }

    public <D> Triple<A, B, D> mapThird(Function<? super C, ? extends D> mapper) {
        return new Triple<>(first, second, mapper.apply(third));
    }

    public <D, E, F> Triple<D, E, F> trimap(Function<? super A, ? extends D> firstMapper,
                                            Function<? super B, ? extends E> secondMapper,
                                            Function<? super C, ? extends F> thirdMapper) {
        return new Triple<>(firstMapper.apply(first), secondMapper.apply(second), thirdMapper.apply(third));
    }

    @Override
    public String toString() {
        return "Triple(" + first + ", " + second + ", " + third + ")";
    }
}