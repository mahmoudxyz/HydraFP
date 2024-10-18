package hydrafp.io.pattern;

import hydrafp.io.core.adt.Either;
import hydrafp.io.core.adt.Option;
import hydrafp.io.core.functions.Function1;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;


public class Match<T> {
    private final T value;
    private final List<Case<T, ?>> cases = new ArrayList<>();
    private Function1<T, ?> otherwise;

    private Match(T value) {
        this.value = value;
    }

    public static <T> Match<T> of(T value) {
        return new Match<>(value);
    }

    public <R> WhenClause<T, R> when(T caseValue) {
        return when(v -> v.equals(caseValue));
    }

    public <R> WhenClause<T, R> when(Predicate<T> predicate) {
        return new WhenClause<>(this, predicate);
    }

    public <R> TypeWhenClause<T, R> whenType(Class<R> type) {
        return new TypeWhenClause<>(this, type);
    }

    public <L, R> EitherLeftWhenClause<T, L, R> whenLeft(Class<L> leftType) {
        return new EitherLeftWhenClause<>(this, leftType);
    }

    public <L, R> EitherRightWhenClause<T, L, R> whenRight(Class<R> rightType) {
        return new EitherRightWhenClause<>(this, rightType);
    }

    public <R> Option<R> otherwise(R defaultValue) {
        return otherwise(ignored -> defaultValue);
    }

    public <R> Option<R> otherwise(Function1<T, R> mapper) {
        this.otherwise = mapper;
        return result();
    }

    @SuppressWarnings("unchecked")
    public <R> Option<R> result() {
        for (Case<T, ?> case_ : cases) {
            if (case_.predicate.test(value)) {
                return Option.of((R) case_.mapper.apply(value));
            }
        }
        if (otherwise != null) {
            return Option.of((R) otherwise.apply(value));
        }
        return Option.of(null);
    }

    <R> void addCase(Predicate<T> predicate, Function1<T, R> mapper) {
        cases.add(new Case<>(predicate, mapper));
    }

    private static class Case<T, R> {
        final Predicate<T> predicate;
        final Function1<T, R> mapper;

        Case(Predicate<T> predicate, Function1<T, R> mapper) {
            this.predicate = predicate;
            this.mapper = mapper;
        }
    }

    public static class WhenClause<T, R> {
        private final Match<T> match;
        private final Predicate<T> predicate;

        WhenClause(Match<T> match, Predicate<T> predicate) {
            this.match = match;
            this.predicate = predicate;
        }

        public Match<T> then(R result) {
            return then(ignored -> result);
        }

        public Match<T> then(Function1<T, R> mapper) {
            match.addCase(predicate, mapper);
            return match;
        }
    }

    public static class TypeWhenClause<T, R> {
        private final Match<T> match;
        private final Class<R> type;

        TypeWhenClause(Match<T> match, Class<R> type) {
            this.match = match;
            this.type = type;
        }

        public Match<T> then(Function1<R, ?> mapper) {
            match.addCase(type::isInstance, v -> mapper.apply(type.cast(v)));
            return match;
        }
    }

    public static class EitherLeftWhenClause<T, L, R> {
        private final Match<T> match;
        private final Class<L> leftType;

        EitherLeftWhenClause(Match<T> match, Class<L> leftType) {
            this.match = match;
            this.leftType = leftType;
        }

        public <U> Match<T> then(Function1<? super L, ? extends U> mapper) {
            match.addCase(
                    v -> v instanceof Either && ((Either<?, ?>) v).isLeft() && leftType.isInstance(((Either<?, ?>) v).fold(l -> l, r -> r)),
                    v -> {
                        Either<L, R> either = (Either<L, R>) v;
                        return either.fold(mapper, r -> null);
                    }
            );
            return match;
        }
    }

    public static class EitherRightWhenClause<T, L, R> {
        private final Match<T> match;
        private final Class<R> rightType;

        EitherRightWhenClause(Match<T> match, Class<R> rightType) {
            this.match = match;
            this.rightType = rightType;
        }

        public <U> Match<T> then(Function1<? super R, ? extends U> mapper) {
            match.addCase(
                    v -> v instanceof Either && !((Either<?, ?>) v).isLeft() && rightType.isInstance(((Either<?, ?>) v).fold(l -> l, r -> r)),
                    v -> {
                        Either<L, R> either = (Either<L, R>) v;
                        return either.fold(l -> null, mapper);
                    }
            );
            return match;
        }
    }
}