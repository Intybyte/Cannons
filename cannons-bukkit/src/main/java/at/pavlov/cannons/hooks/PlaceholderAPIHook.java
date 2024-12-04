package at.pavlov.cannons.hooks;

import at.pavlov.cannons.Cannons;
import at.pavlov.cannons.container.ItemHolder;
import at.pavlov.internal.Hook;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public class PlaceholderAPIHook extends PlaceholderExpansion implements Hook<Void> {
    private final Cannons plugin;
    private boolean working = false;

    public PlaceholderAPIHook(Cannons plugin) {
        this.plugin = plugin;
    }

    @Override
    public Void hook() {
        return null;
    }

    @Override
    public void onEnable() {
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            this.register();
            working = true;
            plugin.logInfo("§a" + enabledMessage());
        }
    }

    @Override
    public void onDisable() {

    }

    @Override
    public boolean active() {
        return working;
    }

    @Override
    public Class<? extends Hook<?>> getTypeClass() {
        return PlaceholderAPIHook.class;
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

        var operated = plugin.getAiming().getCannonInAimingMode(player.getPlayer());
        if (operated == null) return null;

        return switch (params) {
            case "name" -> operated.getCannonName();
            case "design" -> operated.getDesignID();
            case "horizontal_angle" -> "" + operated.getHorizontalAngle();
            case "vertical_angle" -> "" + operated.getVerticalAngle();
            case "temperature" ->  "" + operated.getAmbientTemperature();
            case "loaded_gunpowder" -> "" + operated.getLoadedGunpowder();
            case "loaded_projectile" -> {
                ItemHolder stack;
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
