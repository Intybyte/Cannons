package mockbukkit;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonObject;
import org.bukkit.Color;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionEffectTypeCategory;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class PotionEffectTypeMock extends PotionEffectType {

    private final int id;
    private final String name;
    private final boolean instant;
    private final Color color;
    private final @NotNull Map<Attribute, AttributeModifier> attributeModifiers;
    private final NamespacedKey key;
    private final PotionEffectTypeCategory category;
    private final String translationKey;


    /**
     * @param key            The namespaced key representing this effect
     * @param id             The magic number representing this effect
     * @param name           The name of this effect
     * @param instant        Whether the effect is instant or not
     * @param color          The color of the effect
     * @param category       The category of the effect
     * @param translationKey The translation key for this potion effect type
     */
    @ApiStatus.Internal
    public PotionEffectTypeMock(@NotNull NamespacedKey key, int id, @NotNull String name, boolean instant, @NotNull Color color, @NotNull PotionEffectTypeCategory category, String translationKey) {
        super();

        this.key = Preconditions.checkNotNull(key);
        this.id = id;
        this.name = Preconditions.checkNotNull(name);
        this.instant = instant;
        this.color = Preconditions.checkNotNull(color);
        this.attributeModifiers = new HashMap<>();
        this.category = Preconditions.checkNotNull(category);
        this.translationKey = translationKey;
    }

    /**
     * Constructs a new {@link PotionEffectTypeMock} with the provided parameters.
     *
     * @param key     The key of the effect type.
     * @param id      The numerical ID of the effect type.
     * @param name    The name of the effect type.
     * @param instant Whether the effect type is instantly applied.
     * @param color   The color of the effect type.
     */
    @Deprecated(forRemoval = true)
    public PotionEffectTypeMock(@NotNull NamespacedKey key, int id, String name, boolean instant, Color color) {
        this(key, id, name, instant, color, PotionEffectTypeCategory.NEUTRAL, "effect.mockbukkit.placeholder");
    }

    /**
     * @param data Json data
     */
    @Deprecated(forRemoval = true)
    public PotionEffectTypeMock(JsonObject data) {
        this(NamespacedKey.fromString(data.get("key").getAsString()), data.get("id").getAsInt(), data.get("name").getAsString(), data.get("instant").getAsBoolean(), Color.fromRGB(data.get("rgb").getAsInt()), PotionEffectTypeCategory.valueOf(data.get("category").getAsString()), data.get("translationKey").getAsString());
    }

    @ApiStatus.Internal
    public static Keyed from(JsonObject data) {
        NamespacedKey key = NamespacedKey.fromString(data.get("key").getAsString());
        int id = data.get("id").getAsInt();
        String name = data.get("name").getAsString();
        boolean instant = data.get("instant").getAsBoolean();
        Color color = Color.fromRGB(data.get("rgb").getAsInt());
        PotionEffectTypeCategory category = PotionEffectTypeCategory.valueOf(data.get("category").getAsString());
        String translationKey = data.get("translationKey").getAsString();
        return new PotionEffectTypeMock(key, id, name, instant, color, category, translationKey);
    }

    @Deprecated
    @Override
    public double getDurationModifier() {
        // This is deprecated and always returns 1.0
        return 1.0;
    }

    @Override
    public @NotNull String getName() {
        return name;
    }

    @Override
    @Deprecated(since = "1.20")
    public int getId() {
        return this.id;
    }

    @Override
    public @NotNull NamespacedKey getKey() {
        return key;
    }

    @Override
    public @NotNull PotionEffect createEffect(int duration, int amplifier) {
        return new PotionEffect(this, isInstant() ? 1 : (int) (duration * getDurationModifier()), amplifier);
    }

    @Override
    public boolean isInstant() {
        return instant;
    }

    @Override
    public @NotNull PotionEffectTypeCategory getCategory() {
        // TODO Auto-generated method stub
        return category;
    }

    @Override
    public @NotNull Color getColor() {
        return color;
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof PotionEffectType) {
            // It would make sense to compare the NamespacedKey here but Spigot stil compares ids
            return id == ((PotionEffectType) obj).getId();
        }

        return false;
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public @NotNull String getTranslationKey() {
        return this.translationKey;
    }

}