package at.pavlov.cannons.projectile.definitions;

import at.pavlov.cannons.Cannons;
import at.pavlov.internal.Key;
import at.pavlov.internal.key.registries.Registries;
import at.pavlov.internal.projectile.definition.CustomProjectileDefinition;
import at.pavlov.internal.projectile.definition.DefaultProjectileDefinition;
import at.pavlov.internal.projectile.definition.ProjectilePhysics;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.HashMap;

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
        ProjectilePhysics dpd = Registries.DEFAULT_PROJECTILE_DEFINITION_REGISTRY.of(entityKey);
        if (dpd == null) {
            dpd = ProjectilePhysics.DEFAULT;
        }

        CustomProjectileDefinition.CustomProjectileDefinitionBuilder builder = CustomProjectileDefinition.builder()
                .key(key)
                .entityKey(entityKey)
                .constantAcceleration(section.getObject("constantAcceleration", Double.class, dpd.getConstantAcceleration()))
                .gravity(section.getDouble("gravity", dpd.getGravity()))
                .drag(section.getDouble("drag", dpd.getDrag()))
                .waterDrag(section.getDouble("waterDrag", dpd.getWaterDrag()))
                .glowing(section.getBoolean("glowing"))
                .onFire(section.getBoolean("onFire"))
                .charged(section.getBoolean("charged"))
                .critical(section.getBoolean("critical"))
                .material(Key.from(section.getString("material", "SNOWBALL")))
                .customModelData(section.getObject("customModelData", Integer.class, null))
                .attributes(getStringDoubleHashMap(section, "attributes"))
                .entityDisplayData(getStringDoubleHashMap(section, "entityDisplayData"));

        Registries.CUSTOM_PROJECTILE_DEFINITION.register(builder.build());
    }

    private static @NotNull HashMap<String, @NotNull Double> getStringDoubleHashMap(ConfigurationSection section, String key) {
        var attributeSection = section.getConfigurationSection(key);
        HashMap<String, @NotNull Double> attributeMap = new HashMap<>();
        if (attributeSection != null) {
            for (var attrName : attributeSection.getKeys(false)) {
                var value = attributeSection.getDouble(attrName);
                attributeMap.put(attrName, value);
            }
        }
        return attributeMap;
    }
}
