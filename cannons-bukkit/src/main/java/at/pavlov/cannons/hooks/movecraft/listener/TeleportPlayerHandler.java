package at.pavlov.cannons.hooks.movecraft.listener;

import at.pavlov.cannons.event.CannonBeforeCreateEvent;
import net.countercraft.movecraft.events.CraftTeleportEntityEvent;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.UUID;

public class TeleportPlayerHandler implements Listener {
    private final HashMap<UUID, Long> playerCannonCooldown = new HashMap<>();

    @EventHandler(ignoreCancelled = true)
    private void handleTeleport(CraftTeleportEntityEvent event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof Player player)) {
            return;
        }

        long tickCooldown = event.getCraft().getTickCooldown() * 50L;
        playerCannonCooldown.put(
            player.getUniqueId(),
            tickCooldown * 2 + System.currentTimeMillis()
        );
    }

    @EventHandler(ignoreCancelled = true)
    private void handleCreation(CannonBeforeCreateEvent event) {
        UUID player = event.getPlayer();
        Long cooldown = playerCannonCooldown.get(player);
        if (cooldown == null) return;

        if (System.currentTimeMillis() < cooldown) {
            event.setCancelled(true);
            event.setMessage(null);
            return;
        }

        playerCannonCooldown.remove(player);
    }
}
