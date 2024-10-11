package hydrafp.io.pattern;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;


public class When<T> {
    private final T subject;
    private final List<Case<T, ?>> cases = new ArrayList<>();
    private Supplier<?> elseCase;

    private When(T subject) {
        this.subject = subject;
    }

    public static <T> When<T> subject(T subject) {
        return new When<>(subject);
    }

    public <R> When<T> is(Object value, Supplier<R> result) {
        cases.add(new Case<>(obj -> obj.equals(value), result));
        return this;
    }

    public <R> When<T> is(Predicate<T> predicate, Supplier<R> result) {
        cases.add(new Case<>(predicate, result));
        return this;
    }

    public <R> When<T> elseIs(Supplier<R> result) {
        this.elseCase = result;
        return this;
    }

    public <R> R eval() {
        for (Case<T, ?> case_ : cases) {
            if (case_.predicate.test(subject)) {
                return (R) case_.result.get();
            }
        }
        if (elseCase != null) {
            return (R) elseCase.get();
        }
        throw new IllegalStateException("No matching case found and no else case provided");
    }

    private static class Case<T, R> {
        final Predicate<T> predicate;
        final Supplier<R> result;

        Case(Predicate<T> predicate, Supplier<R> result) {
            this.predicate = predicate;
            this.result = result;
        }
    }
}