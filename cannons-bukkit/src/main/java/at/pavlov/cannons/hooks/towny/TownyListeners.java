package at.pavlov.cannons.hooks.towny;

import at.pavlov.cannons.config.Config;
import at.pavlov.cannons.event.CannonUseEvent;
import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.utils.CombatUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.UUID;

import static at.pavlov.cannons.Enum.InteractAction.adjustPlayer;
import static at.pavlov.cannons.Enum.InteractAction.fireRightClickTigger;

public class TownyListeners implements Listener {
    private static final Config config = Config.getInstance();
    private static final TownyAPI api = TownyAPI.getInstance();

    @EventHandler
    public void onAction(CannonUseEvent event) {
        Location location = event.getCannon().getLocation();
        TownyAllowCannon tac = config.getTownyAllowedPlayers();
        if (tac == TownyAllowCannon.ALL) {
            return;
        }

        Town town = api.getTown(location);
        if (town == null) {
            return;
        }

        UUID playerUUID = event.getPlayer();
        if (playerUUID == null) {
            return;
        }

        Player player = Bukkit.getPlayer(playerUUID);
        if (player == null) {
            event.setCancelled(true);
            return;
        }

        if (player.hasPermission("cannons.admin.*")) {
            return;
        }

        if (town.hasResident(playerUUID)) {
            return;
        }

        // we already did town check,
        // if it failed and tac is set to TOWN we are done here
        if (tac == TownyAllowCannon.TOWN) {
            event.setCancelled(true);
            player.sendMessage("§4[Cannons] No permission to interact with this cannons. (TownyAllowCannon.TOWN)");
            return;
        }

        Resident resident = api.getResident(playerUUID);
        if (resident == null) { //this shouldn't happen tbh
            return;
        }

        Town otherTown = resident.getTownOrNull();;
        if (otherTown == null) {
            event.setCancelled(true);
            player.sendMessage("§4[Cannons] No permission to interact with this cannons. (No town)");
            return;
        }

        if (CombatUtil.isAlly(town, otherTown)) {
            return;
        }

        event.setCancelled(true);
        player.sendMessage("§4[Cannons] No permission to interact with this cannons. (TownyAllowCannon.ALLY)");
    }

}
