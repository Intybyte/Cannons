package at.pavlov.cannons.dao;

import at.pavlov.cannons.Cannons;
import org.bukkit.Bukkit;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AsyncTaskManager {
    public static final ExecutorService executor = Executors.newFixedThreadPool(2);
    public static final Executor bukkit = Bukkit.getScheduler().getMainThreadExecutor(Cannons.getPlugin());
}
