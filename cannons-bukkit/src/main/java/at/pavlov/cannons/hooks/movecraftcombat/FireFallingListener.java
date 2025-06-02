package at.pavlov.cannons.hooks.movecraftcombat;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;

public class FireFallingListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onFireFall(EntityChangeBlockEvent event) {
        if (event.getTo() != Material.FIRE) {
            return;
        }

        BlockIgniteEvent ignite = new BlockIgniteEvent(event.getBlock(), BlockIgniteEvent.IgniteCause.EXPLOSION, null, null);
        Bukkit.getPluginManager().callEvent(ignite);
    }
}
