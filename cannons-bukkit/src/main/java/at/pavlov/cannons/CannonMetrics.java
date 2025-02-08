package at.pavlov.cannons;

import at.pavlov.internal.Hook;
import at.pavlov.internal.HookManager;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.AdvancedPie;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public class CannonMetrics extends Metrics {
    public CannonMetrics(JavaPlugin plugin) {
        super(plugin, 23139);
    }

    public void setupCharts() {
        HookManager hookManager = Cannons.getPlugin().getHookManager();

        this.addCustomChart(
                new AdvancedPie("hooks", () -> {

                    Map<String, Integer> result = new HashMap<>();
                    if (!hookManager.isActive()) {
                        result.put("None", 1);
                        return result;
                    }

                    for (Hook<?> hook : hookManager.hookMap().values()) {
                        final int status = hook.active() ? 1 : 0;
                        result.put(hook.getTypeClass().getSimpleName(), status);
                    }

                    return result;
                })
        );
    }
}
