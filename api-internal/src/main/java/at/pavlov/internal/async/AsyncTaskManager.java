package at.pavlov.internal.async;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AsyncTaskManager {
    private static AsyncTaskManager instance;
    public final ExecutorService async;

    private AsyncTaskManager(int threadCount) {
        async = Executors.newFixedThreadPool(threadCount);
    }

    public static AsyncTaskManager get() {
        return instance;
    }

    public static void initialize(int threads) {
        if (instance != null) {
            return;
        }

        instance = new AsyncTaskManager(threads);
    }
}
