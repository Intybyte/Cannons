package at.pavlov.internal.cannons.data;

import lombok.Data;

import java.util.HashSet;
import java.util.UUID;

@Data public class WhitelistData {
    // the last player which was added or removed from the whitelist
    private UUID lastWhitelisted;
    //a sentry cannon will not target a whitelisted player
    private final HashSet<UUID> whitelist = new HashSet<>();

    private boolean whitelistUpdated = true;

    public void add(UUID player) {
        whitelist.add(player);
    }

    public void remove(UUID player) {
        whitelist.remove(player);
    }

    public void clear() {
        whitelist.clear();
    }
}
