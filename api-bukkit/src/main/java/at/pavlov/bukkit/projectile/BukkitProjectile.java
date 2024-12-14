package at.pavlov.bukkit.projectile;

import at.pavlov.bukkit.builders.ParticleBuilder;
import at.pavlov.bukkit.container.BukkitSoundHolder;
import at.pavlov.bukkit.container.BukkitItemHolder;
import at.pavlov.bukkit.container.BukkitEntityHolder;
import at.pavlov.bukkit.container.BukkitSpawnMaterialHolder;
import at.pavlov.internal.projectile.Projectile;
import org.bukkit.FireworkEffect;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

public class BukkitProjectile extends Projectile<
        EntityType,
        PotionEffectType,
        BlockData,
        ParticleBuilder,
        FireworkEffect.Builder,
        BukkitItemHolder,
        BukkitSpawnMaterialHolder,
        BukkitSoundHolder,
        BukkitEntityHolder
        > implements Cloneable {

    public BukkitProjectile(String id) {
        super(id);
    }

    /**
     * returns true if the player has permission to use that projectile
     *
     * @param player who tried to load this projectile
     * @return true if the player can load this projectile
     */
    public boolean hasPermission(Player player) {
        if (player == null) return true;

        for (String perm : permissionLoad) {
            if (!player.hasPermission(perm)) {
                //missing permission
                return false;
            }
        }
        //player has all permissions
        return true;
    }

    @Override
    public BukkitProjectile clone() {
        try {
            return (BukkitProjectile) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
