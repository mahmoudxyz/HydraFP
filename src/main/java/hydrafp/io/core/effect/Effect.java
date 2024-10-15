package hydrafp.io.core.effect;

import hydrafp.io.core.adt.Try;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public interface Effect<A> {
    <B> Effect<B> map(Function<? super A, ? extends B> mapper);
    <B> Effect<B> flatMap(Function<? super A, ? extends Effect<B>> mapper);
    Effect<A> recover(Function<? super Throwable, ? extends A> recovery);
    CompletableFuture<A> runAsync(Runtime runtime);
    A unsafeRunSync(Runtime runtime);
    Try<A> attempt(Runtime runtime);
}