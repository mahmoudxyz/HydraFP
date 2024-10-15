package hydrafp.io.core.effect;

import hydrafp.io.core.adt.Either;
import hydrafp.io.core.adt.Option;

interface IOEffect<A> extends Effect<A> {
    Either<Throwable, A> toEither(Runtime runtime);
    Option<A> toOption(Runtime runtime);
}