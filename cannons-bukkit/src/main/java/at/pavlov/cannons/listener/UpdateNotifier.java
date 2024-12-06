package at.pavlov.cannons.listener;

import at.pavlov.cannons.Cannons;
import at.pavlov.cannons.dao.AsyncTaskManager;
import at.pavlov.internal.ModrinthUpdateChecker;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.HashSet;
import java.util.UUID;

public class UpdateNotifier implements Listener {
    private final HashSet<UUID> notified = new HashSet<>();
    private final Cannons plugin;

    public UpdateNotifier(Cannons plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (!player.isOp()) {
            return;
        }

        UUID uuid = player.getUniqueId();
        if (notified.contains(uuid)) {
            return;
        }

        if (plugin.getIsLatest() == Boolean.FALSE) {
            notified.add(uuid);
            String download = plugin.getUpdateChecker().getDownloadUrl();
            player.sendMessage(ChatColor.YELLOW + "[Cannons] You are not on the latest version. Download new version here: " + download);
        }

    }
}
