package hydrafp.io.core.adt;

import java.util.function.Function;

public abstract class Either<L, R> {
    private Either() {}

    public abstract <T> T fold(Function<? super L, ? extends T> leftMapper, Function<? super R, ? extends T> rightMapper);

    public abstract <T> Either<L, T> map(Function<? super R, ? extends T> mapper);

    public abstract <T> Either<L, T> flatMap(Function<? super R, ? extends Either<L, T>> mapper);

    public abstract boolean isLeft();

    public abstract boolean isRight();

    public abstract L getLeft();

    public abstract R getRight();

    public R getRightOrElse(R defaultValue) {
        return fold(l -> defaultValue, r -> r);
    }

    public L getLeftOrElse(L defaultValue) {
        return fold(l -> l, r -> defaultValue);
    }

    public static <L, R> Either<L, R> left(L value) {
        return new Left<>(value);
    }

    public static <L, R> Either<L, R> right(R value) {
        return new Right<>(value);
    }

    private static final class Left<L, R> extends Either<L, R> {
        private final L value;

        private Left(L value) {
            this.value = value;
        }

        @Override
        public <T> T fold(Function<? super L, ? extends T> leftMapper, Function<? super R, ? extends T> rightMapper) {
            return leftMapper.apply(value);
        }

        @Override
        public <T> Either<L, T> map(Function<? super R, ? extends T> mapper) {
            return new Left<>(value);
        }

        @Override
        public <T> Either<L, T> flatMap(Function<? super R, ? extends Either<L, T>> mapper) {
            return new Left<>(value);
        }

        @Override
        public boolean isLeft() {
            return true;
        }

        @Override
        public boolean isRight() {
            return false;
        }

        @Override
        public L getLeft() {
            return value;
        }

        @Override
        public R getRight() {
            throw new UnsupportedOperationException("Cannot get right value from Left");
        }
    }

    private static final class Right<L, R> extends Either<L, R> {
        private final R value;

        private Right(R value) {
            this.value = value;
        }

        @Override
        public <T> T fold(Function<? super L, ? extends T> leftMapper, Function<? super R, ? extends T> rightMapper) {
            return rightMapper.apply(value);
        }

        @Override
        public <T> Either<L, T> map(Function<? super R, ? extends T> mapper) {
            return new Right<>(mapper.apply(value));
        }

        @Override
        public <T> Either<L, T> flatMap(Function<? super R, ? extends Either<L, T>> mapper) {
            return mapper.apply(value);
        }

        @Override
        public boolean isLeft() {
            return false;
        }

        @Override
        public boolean isRight() {
            return true;
        }

        @Override
        public L getLeft() {
            throw new UnsupportedOperationException("Cannot get left value from Right");
        }

        @Override
        public R getRight() {
            return value;
        }
    }
}