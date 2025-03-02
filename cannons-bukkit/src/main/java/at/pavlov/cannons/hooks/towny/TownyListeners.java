package at.pavlov.cannons.hooks.towny;

import at.pavlov.cannons.config.Config;
import at.pavlov.cannons.event.CannonUseEvent;
import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.utils.CombatUtil;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.UUID;

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
        for (Resident resident : town.getResidents()) {
            if (resident.getUUID() == playerUUID) {
                return;
            }
        }

        if (tac == TownyAllowCannon.TOWN) {
            event.setCancelled(true);
            return;
        }

        Resident resident = api.getResident(playerUUID);
        if (resident == null) {
            //this shouldn't happen tbh
            return;
        }

        Town otherTown;
        try {
            otherTown = resident.getTown();
        } catch (Exception e) {
            event.setCancelled(true);
            return;
        }

        if (CombatUtil.isAlly(town, otherTown)) {
            return;
        }

        event.setCancelled(true);
    }

}
