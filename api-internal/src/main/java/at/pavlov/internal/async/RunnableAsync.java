package at.pavlov.internal.async;

import java.util.concurrent.Future;

public interface RunnableAsync extends Runnable {
    default Future<?> runTaskAsynchronously() {
        return AsyncTaskManager.get().async.submit(this);
    }
}
