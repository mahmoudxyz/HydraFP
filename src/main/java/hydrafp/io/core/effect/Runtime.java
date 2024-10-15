package hydrafp.io.core.effect;

import java.util.concurrent.ExecutorService;

interface Runtime {
    java.util.concurrent.ExecutorService getExecutor();
}
