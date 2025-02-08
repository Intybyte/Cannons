package at.pavlov.cannons;

import at.pavlov.cannons.cannon.CannonDesign;
import at.pavlov.cannons.cannon.CannonManager;
import at.pavlov.cannons.cannon.DesignStorage;
import at.pavlov.cannons.projectile.Projectile;
import at.pavlov.cannons.projectile.ProjectileProperties;
import at.pavlov.cannons.projectile.ProjectileStorage;
import at.pavlov.cannons.utils.ParseUtils;
import at.pavlov.internal.Hook;
import at.pavlov.internal.HookManager;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.AdvancedPie;
import org.bstats.charts.SimplePie;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

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

        this.addCustomChart(
                new SimplePie("cannons_amount", () -> getRange(CannonManager.getCannonList().size()))
        );

        this.addCustomChart(
                new SimplePie("design_amount", () -> getRange(DesignStorage.getInstance().getCannonDesignList().size()))
        );

        this.addCustomChart(
                new SimplePie("projectile_amount", () -> getRange(ProjectileStorage.getProjectileList().size()))
        );

        this.addCustomChart(
                new AdvancedPie("gunpowder_materials", () -> {
                    Map<String, Integer> result = new HashMap<>();

                    for (CannonDesign design : DesignStorage.getInstance().getCannonDesignList()) {
                        if (!design.isGunpowderNeeded()) {
                            result.compute("None", this::adderCompute);
                        } else {
                            Material gunpowder = design.getGunpowderType().getType();
                            String name = ParseUtils.normalizeName(gunpowder.name());
                            result.compute(name, this::adderCompute);
                        }
                    }

                    return result;
                })
        );

        this.addCustomChart(
                new AdvancedPie("projectile_entities", () -> {
                    Map<String, Integer> result = new HashMap<>();

                    ProjectileStorage.getProjectileList().stream()
                            .map(Projectile::getProjectileEntity)
                            .filter(Objects::nonNull)
                            .map(EntityType::getKey)
                            .map(NamespacedKey::getKey)
                            .map(ParseUtils::normalizeName)
                            .forEach(entityString -> result.compute(entityString, this::adderCompute));

                    return result;
                })
        );

        this.addCustomChart(
                new AdvancedPie("projectile_properties", () -> {
                    Map<String, Integer> result = new HashMap<>();

                    for (Projectile projectile : ProjectileStorage.getProjectileList()) {
                        var list = projectile.getPropertyList();
                        if (list.isEmpty()) {
                            result.compute("None", this::adderCompute);
                            continue;
                        }

                        list.stream()
                                .map(ProjectileProperties::getName)
                                .map(ParseUtils::normalizeName)
                                .forEach((name) -> result.compute(name, this::adderCompute));
                    }

                    return result;
                })
        );
    }

    private Integer adderCompute(String key, Integer value) {
        return value == null ? 1 : value + 1;
    }

    private static String getRange(int amount) {
        int range = amount - (amount % 10);
        return String.valueOf(range);
    }
}
