package at.pavlov.cannons.dao;

import at.pavlov.cannons.Cannons;
import com.github.Anon8281.universalScheduler.UniversalScheduler;
import com.github.Anon8281.universalScheduler.scheduling.schedulers.TaskScheduler;
import org.bukkit.Bukkit;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

public class MainTaskManager {
    protected static MainTaskManager instance;
    public final TaskScheduler scheduler;
    public final Executor main;

    private MainTaskManager(Cannons cannons) {
        scheduler = UniversalScheduler.getScheduler(cannons);
        main = scheduler::runTask;
    }

    public static void initialize(Cannons cannons) {
        if (instance != null) {
            return;
        }

        instance = new MainTaskManager(cannons);
    }

    public static MainTaskManager get() {
        return instance;
    }

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
