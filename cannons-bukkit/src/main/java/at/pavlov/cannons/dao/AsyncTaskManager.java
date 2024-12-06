package at.pavlov.cannons.dao;

import at.pavlov.cannons.Cannons;
import com.github.Anon8281.universalScheduler.UniversalScheduler;
import com.github.Anon8281.universalScheduler.scheduling.schedulers.TaskScheduler;
import lombok.Setter;
import org.bukkit.Bukkit;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;

public class AsyncTaskManager {
    protected static AsyncTaskManager instance;
    @Setter
    protected static int threadCount = 2;

    private AsyncTaskManager(Cannons cannons) {
        async = Executors.newFixedThreadPool(threadCount);
        scheduler = UniversalScheduler.getScheduler(cannons);
        main = scheduler::runTask;
    }

    public static AsyncTaskManager get() {
        return instance;
    }

    public static void initialize(Cannons cannons) {
        if (instance != null) {
            return;
        }

        instance = new AsyncTaskManager(cannons);
    }

    public final ExecutorService async;
    public final TaskScheduler scheduler;
    public final Executor main;

    public void fireSyncRunnable(Runnable runnable) {

        if (Bukkit.isPrimaryThread()) {
            runnable.run();
        } else {
            CompletableFuture.runAsync(runnable, main).join();
        }
    }

    public <T> T fireSyncSupplier(Supplier<T> supplier) {
        if (Bukkit.isPrimaryThread()) {
            return supplier.get();
        } else {
            return CompletableFuture.supplyAsync(supplier, main).join();
        }
    }
}
