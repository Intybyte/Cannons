package at.pavlov.internal.cannons;

import at.pavlov.cannons.dao.AsyncTaskManager;

import java.util.concurrent.Future;

public interface RunnableAsync extends Runnable {
    default Future<?> runTaskAsynchronously() {
        return AsyncTaskManager.get().async.submit(this);
    }
}
