package at.pavlov.cannons.dao;

import at.pavlov.cannons.Cannons;
import org.bukkit.Bukkit;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;

public class AsyncTaskManager {
    public static final ExecutorService executor = Executors.newFixedThreadPool(2);
    public static final Executor bukkit = Bukkit.getScheduler().getMainThreadExecutor(Cannons.getPlugin());

    public static void fireSyncRunnable(Runnable runnable) {

        if (Bukkit.isPrimaryThread()) {
            runnable.run();
        } else {
            CompletableFuture.runAsync(runnable, bukkit).join();
        }
    }

    public static <T> T fireSyncSupplier(Supplier<T> supplier) {
        if (Bukkit.isPrimaryThread()) {
            return supplier.get();
        } else {
            return CompletableFuture.supplyAsync(supplier, bukkit).join();
        }
    }
}
