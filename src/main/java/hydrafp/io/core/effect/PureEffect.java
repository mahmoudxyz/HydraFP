package hydrafp.io.core.effect;

import hydrafp.io.core.adt.Either;
import hydrafp.io.core.adt.Option;
import hydrafp.io.core.adt.Try;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

class PureEffect<A> implements IOEffect<A> {
    private final A value;

    PureEffect(A value) {
        this.value = value;
    }

    @Override
    public <B> Effect<B> map(Function<? super A, ? extends B> mapper) {
        return new PureEffect<>(mapper.apply(value));
    }

    @Override
    public <B> Effect<B> flatMap(Function<? super A, ? extends Effect<B>> mapper) {
        return mapper.apply(value);
    }

    @Override
    public Effect<A> recover(Function<? super Throwable, ? extends A> recovery) {
        return this;
    }

    @Override
    public CompletableFuture<A> runAsync(Runtime runtime) {
        return CompletableFuture.completedFuture(value);
    }

    @Override
    public A unsafeRunSync(Runtime runtime) {
        return value;
    }

    @Override
    public Try<A> attempt(Runtime runtime) {
        return Try.success(value);
    }

    @Override
    public Either<Throwable, A> toEither(Runtime runtime) {
        return Either.right(value);
    }

    @Override
    public Option<A> toOption(Runtime runtime) {
        return Option.some(value);
    }
}

