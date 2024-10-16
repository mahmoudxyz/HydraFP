package hydrafp.io.dynamic;

import hydrafp.io.core.adt.Option;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.stream.Collectors;

public final class DynamicRecordCreator {
    private DynamicRecordCreator() {}

    public static DynamicRecord createRecord(Map<String, Object> values) {
        Objects.requireNonNull(values, "values must not be null");
        return (DynamicRecord) Proxy.newProxyInstance(
                DynamicRecord.class.getClassLoader(),
                new Class<?>[] { DynamicRecord.class },
                new DynamicRecordInvocationHandler(values)
        );
    }

    public interface DynamicRecord {
        Option<Object> get(String key);
    }

    private record DynamicRecordInvocationHandler(Map<String, Object> values) implements InvocationHandler {
            private DynamicRecordInvocationHandler(Map<String, Object> values) {
                this.values = Map.copyOf(values);
            }

            @Override
            public Object invoke(Object proxy, Method method, Object[] args) {
                return switch (method.getName()) {
                    case "get" -> Option.of(values.get(args[0]));
                    case "toString" -> toStringImpl();
                    case "hashCode" -> Objects.hash(values.values().toArray());
                    case "equals" -> equalsImpl(proxy, args[0]);
                    default -> Option.of(null);
                };
            }

            private String toStringImpl() {
                String content = new TreeMap<>(values).entrySet().stream()
                        .map(e -> e.getKey() + "=" + e.getValue())
                        .collect(Collectors.joining(", "));
                return "DynamicRecord{" + content + "}";
            }

            private boolean equalsImpl(Object proxy, Object other) {
                if (proxy == other) return true;
                if (!(other instanceof DynamicRecord otherRecord)) return false;
                return values.entrySet().stream()
                        .allMatch(entry -> otherRecord.get(entry.getKey()).getOrElse(null) == entry.getValue());
            }
        }
}