package at.pavlov.cannons.dao;

import at.pavlov.cannons.Cannons;
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

    private AsyncTaskManager() {
        async =  Executors.newFixedThreadPool(threadCount);
    }

    public static AsyncTaskManager get() {
        if (instance != null) {
            return instance;
        }

        instance = new AsyncTaskManager();
        return instance;
    }

    public final ExecutorService async;
    public final Executor bukkit = Bukkit.getScheduler().getMainThreadExecutor(Cannons.getPlugin());

    public void fireSyncRunnable(Runnable runnable) {

        if (Bukkit.isPrimaryThread()) {
            runnable.run();
        } else {
            CompletableFuture.runAsync(runnable, bukkit).join();
        }
    }

    public <T> T fireSyncSupplier(Supplier<T> supplier) {
        if (Bukkit.isPrimaryThread()) {
            return supplier.get();
        } else {
            return CompletableFuture.supplyAsync(supplier, bukkit).join();
        }
    }
}
