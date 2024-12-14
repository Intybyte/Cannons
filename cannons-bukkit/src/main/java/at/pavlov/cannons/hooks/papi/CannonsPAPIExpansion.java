package at.pavlov.cannons.hooks.papi;

import at.pavlov.bukkit.container.BukkitItemHolder;
import at.pavlov.cannons.Aiming;
import at.pavlov.cannons.Cannons;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public class CannonsPAPIExpansion extends PlaceholderExpansion {
    private final Cannons plugin;

    public CannonsPAPIExpansion(Cannons cannons) {
        this.plugin = cannons;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "cannons";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Vaan1310";
    }

    @Override
    public @NotNull String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String params) {
        if (player == null) return null;

        var operated = Aiming.getInstance().getCannonInAimingMode(player.getPlayer());
        if (operated == null) return null;

        return switch (params) {
            case "name" -> operated.getCannonName();
            case "design" -> operated.getDesignID();
            case "horizontal_angle" -> "" + operated.getHorizontalAngle();
            case "vertical_angle" -> "" + operated.getVerticalAngle();
            case "temperature" ->  "" + operated.getAmbientTemperature();
            case "loaded_gunpowder" -> "" + operated.getLoadedGunpowder();
            case "loaded_projectile" -> {
                BukkitItemHolder stack;
                try {
                    stack = operated.getLoadedProjectile().getLoadingItem();
                } catch (Exception e) {
                    yield  "None";
                }

                if (stack.hasDisplayName()) {
                    yield  stack.getDisplayName();
                }

                String materialName = stack.toString().toLowerCase(Locale.ROOT).replace(":", "");
                var normalName = "";

                for (String word : materialName.split("_")) {
                    //capitalize first letter
                    normalName += Character.toUpperCase(word.charAt(0));
                    for (int i = 1; i < word.length(); i++) {
                        normalName += word.charAt(i);
                    }
                }

                yield  normalName;
            }
            default -> null;
        };
    }
}
