package at.pavlov.cannons.scheduler;

import at.pavlov.cannons.dao.AsyncTaskManager;

import java.util.concurrent.Future;

public interface RunnableAsync extends Runnable {
    default Future<?> runTaskAsynchronously() {
        return AsyncTaskManager.get().async.submit(this);
    }
}
