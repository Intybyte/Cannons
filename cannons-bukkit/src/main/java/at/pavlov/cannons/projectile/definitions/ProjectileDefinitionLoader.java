package at.pavlov.cannons.projectile.definitions;

import at.pavlov.cannons.Cannons;
import at.pavlov.internal.Key;
import at.pavlov.internal.key.registries.Registries;
import at.pavlov.internal.projectile.definition.CustomProjectileDefinition;
import at.pavlov.internal.projectile.definition.DefaultProjectileDefinition;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class ProjectileDefinitionLoader {
    public static void reload() {
        Registries.CUSTOM_PROJECTILE_DEFINITION.clear();
        load();
    }

    public static void load() {
        var pl = Cannons.getPlugin();
        var path = new File(pl.getDataFolder(), "projectile_definitions.yml");
        if (!path.exists()) {
            pl.saveResource("projectile_definitions.yml", false);
        }

        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(path);
        for (String node : cfg.getKeys(false)) {
            loadEntry(cfg, node);
        }
    }

    private static void loadEntry(YamlConfiguration cfg, String node) {
        ConfigurationSection section = cfg.getConfigurationSection(node);
        if (section == null) {
            throw new RuntimeException("Impossible but alr");
        }

        Key key = Key.from(node);
        Key entityKey = Key.from(section.getString("entity", "SNOWBALL"));
        DefaultProjectileDefinition dpd = Registries.DEFAULT_PROJECTILE_DEFINITION_REGISTRY.of(entityKey);
        if (dpd == null) {
            throw new RuntimeException("Default Projectile Definition not found, invalid entity?");
        }

        CustomProjectileDefinition definition = CustomProjectileDefinition.builder()
                .key(key)
                .entityKey(entityKey)
                .constantAcceleration(section.getObject("constantAcceleration", Double.class, null))
                .gravity(section.getDouble("gravity", dpd.getGravity()))
                .drag(section.getDouble("drag", dpd.getDrag()))
                .waterDrag(section.getDouble("waterDrag", dpd.getWaterDrag()))
                .glowing(section.getBoolean("glowing"))
                .onFire(section.getBoolean("onFire"))
                .charged(section.getBoolean("charged"))
                .critical(section.getBoolean("critical"))
                .material(Key.from(section.getString("material", "SNOWBALL")))
                .customModelData(section.getObject("customModelData", Integer.class, null))
                .build();

        Registries.CUSTOM_PROJECTILE_DEFINITION.register(definition);
    }
}
