package hydrafp.io.core.effect;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SimpleRuntime implements Runtime {
    private final ExecutorService executor;

    public SimpleRuntime() {
        int threadPoolSize = java.lang.Runtime.getRuntime().availableProcessors();
        this.executor = Executors.newFixedThreadPool(threadPoolSize);
    }

    SimpleRuntime(int threadPoolSize) {
        this.executor = Executors.newFixedThreadPool(threadPoolSize);
    }

    @Override
    public ExecutorService getExecutor() {
        return executor;
    }

    public void shutdown() {
        executor.shutdown();
    }
}
