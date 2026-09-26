package at.pavlov.cannons.schematic;

import at.pavlov.cannons.Cannons;
import lombok.AllArgsConstructor;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.function.Supplier;

@AllArgsConstructor
public class RetryUntil extends BukkitRunnable {
    private final Supplier<Boolean> booleanSupplier;
    private final Runnable runnable;

    public RetryUntil(Supplier<Boolean> booleanSupplier) {
        this(booleanSupplier, () -> {});
    }

    @Override
    public void run() {
        if (booleanSupplier.get()) return;

        runnable.run();
        cancel();
    }

    public void start() {
        runTaskTimer(Cannons.getPlugin(), 1L, 4L);
    }
}
