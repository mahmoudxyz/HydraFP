package hydrafp.io.core.effect;

import hydrafp.io.core.adt.Either;
import hydrafp.io.core.adt.Option;
import hydrafp.io.core.adt.Try;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

class IOEffectImpl<A> implements IOEffect<A> {
    private final Function<Runtime, Try<A>> io;

    IOEffectImpl(Function<Runtime, Try<A>> io) {
        this.io = io;
    }

    @Override
    public <B> Effect<B> map(Function<? super A, ? extends B> mapper) {
        return new IOEffectImpl<>(runtime -> io.apply(runtime).map(mapper));
    }

    @Override
    public <B> Effect<B> flatMap(Function<? super A, ? extends Effect<B>> mapper) {
        return new IOEffectImpl<>(runtime ->
                io.apply(runtime).flatMap(a -> {
                    Effect<B> effect = mapper.apply(a);
                    return ((IOEffectImpl<B>)effect).io.apply(runtime);
                })
        );
    }

    @Override
    public Effect<A> recover(Function<? super Throwable, ? extends A> recovery) {
        return new IOEffectImpl<>(runtime -> io.apply(runtime).recover(recovery));
    }


    @Override
    public CompletableFuture<A> runAsync(Runtime runtime) {
        return CompletableFuture.supplyAsync(() -> io.apply(runtime).get(), runtime.getExecutor());
    }

    @Override
    public A unsafeRunSync(Runtime runtime) {
        return io.apply(runtime).get();
    }

    @Override
    public Try<A> attempt(Runtime runtime) {
        return io.apply(runtime);
    }

    @Override
    public Either<Throwable, A> toEither(Runtime runtime) {
        return io.apply(runtime).toEither(Function.identity());
    }

    @Override
    public Option<A> toOption(Runtime runtime) {
        return io.apply(runtime).toOption();
    }
}
