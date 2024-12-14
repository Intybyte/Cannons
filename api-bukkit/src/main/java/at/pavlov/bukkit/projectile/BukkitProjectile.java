package at.pavlov.bukkit.projectile;

import at.pavlov.bukkit.builders.ParticleBuilder;
import at.pavlov.bukkit.container.BukkitSoundHolder;
import at.pavlov.bukkit.container.BukkitItemHolder;
import at.pavlov.bukkit.container.BukkitEntityHolder;
import at.pavlov.bukkit.container.BukkitSpawnMaterialHolder;
import at.pavlov.internal.CannonLogger;
import at.pavlov.internal.enums.ProjectileProperties;
import at.pavlov.internal.projectile.Projectile;
import lombok.Data;
import org.bukkit.FireworkEffect;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

public class BukkitProjectile extends Projectile<
        Entity,
        PotionEffectType,
        BlockData,
        ParticleBuilder,
        FireworkEffect.Builder,
        BukkitItemHolder,
        BukkitSpawnMaterialHolder,
        BukkitSoundHolder
        > {

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
}
