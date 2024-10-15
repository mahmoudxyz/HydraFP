package hydrafp.io.core.effect;

import hydrafp.io.core.adt.Either;
import hydrafp.io.core.adt.Try;

import java.util.function.Function;
import java.util.function.Supplier;

public class Effects {
    public static <A> Effect<A> pure(A value) {
        return new PureEffect<>(value);
    }

    public static <A> Effect<A> fromIO(Function<Runtime, A> io) {
        return new IOEffectImpl<>(runtime -> Try.of(() -> io.apply(runtime)));
    }

    public static <A> Effect<A> fromTry(Try<A> try_) {
        return new IOEffectImpl<>(x -> try_);
    }
    public static <A> Effect<A> fail(Throwable error) {
        return new IOEffectImpl<>(runtime -> Try.failure(error));
    }

    public static <L, A> Effect<A> fromEither(Either<L, A> either) {
        return new IOEffectImpl<>(x -> either.fold(
                left -> Try.failure(new RuntimeException("Left value: " + left)),
                Try::success
        ));
    }

    public static <A> Effect<A> delay(Supplier<A> supplier) {
        return fromIO(r -> supplier.get());
    }

    public static <A> Effect<A> defer(Supplier<Effect<A>> effectSupplier) {
        return new IOEffectImpl<>(runtime -> {
            try {
                Effect<A> effect = effectSupplier.get();
                return Try.of(() -> effect.unsafeRunSync(runtime));
            } catch (Throwable t) {
                return Try.failure(t);
            }
        });
    }

    public static <A> Effect<A> deferMemoized(Supplier<Effect<A>> effectSupplier) {
        return new IOEffectImpl<>(new Function<Runtime, Try<A>>() {
            private volatile Try<Effect<A>> memoizedEffect = null;

            @Override
            public Try<A> apply(Runtime runtime) {
                if (memoizedEffect == null) {
                    synchronized (this) {
                        if (memoizedEffect == null) {
                            memoizedEffect = Try.of(effectSupplier);
                        }
                    }
                }
                return memoizedEffect.flatMap(effect -> Try.of(() -> effect.unsafeRunSync(runtime)));
            }
        });
    }
    public static <A> Effect<A> retry(Effect<A> effect, int maxAttempts) {
        return new IOEffectImpl<>(runtime -> {
            Try<A> result = Try.failure(new RuntimeException("Initial failure"));
            for (int i = 0; i < maxAttempts && result.isFailure(); i++) {
                result = Try.of(() -> effect.unsafeRunSync(runtime));
            }
            return result;
        });
    }

    public static <A> Effect<A> memoize(Effect<A> effect) {
        return new IOEffectImpl<>(new Function<Runtime, Try<A>>() {
            private volatile Try<A> result = null;

            @Override
            public Try<A> apply(Runtime runtime) {
                if (result == null) {
                    synchronized (this) {
                        if (result == null) {
                            result = Try.of(() -> effect.unsafeRunSync(runtime));
                        }
                    }
                }
                return result;
            }
        });
    }
}