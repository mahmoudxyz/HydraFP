package hydrafp.io.core.tco;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public class Rec<T> {
    private final List<Function<T, T>> steps;
    private final Predicate<T> isDone;

    private Rec(List<Function<T, T>> steps, Predicate<T> isDone) {
        this.steps = steps;
        this.isDone = isDone;
    }

    public static <T> Rec<T> when(Predicate<T> isDone) {
        return new Rec<>(new ArrayList<>(), isDone);
    }

    public Rec<T> step(Function<T, T> step) {
        List<Function<T, T>> newSteps = new ArrayList<>(this.steps);
        newSteps.add(step);
        return new Rec<>(newSteps, this.isDone);
    }

    public Function<T, T> build() {
        return input -> {
            T current = input;
            while (!isDone.test(current)) {
                for (Function<T, T> step : steps) {
                    current = step.apply(current);
                    if (isDone.test(current)) {
                        break;
                    }
                }
            }
            return current;
        };
    }
}