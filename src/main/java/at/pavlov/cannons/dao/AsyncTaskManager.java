package at.pavlov.cannons.dao;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AsyncTaskManager {
    public static final ExecutorService executor = Executors.newFixedThreadPool(2);
}
