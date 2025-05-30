package at.pavlov.internal.cannon.holders;

import at.pavlov.internal.cannon.data.WhitelistData;

import java.util.HashSet;
import java.util.UUID;

public interface WhitelistDataHolder {
    WhitelistData getWhitelistData();
    void setWhitelistData(WhitelistData data);

    default HashSet<UUID> getWhitelist() {
        return getWhitelistData().getWhitelist();
    }

    default void addWhitelistPlayer(UUID playerUID) {
        getWhitelistData().setLastWhitelisted(playerUID);
        getWhitelistData().add(playerUID);
        this.hasWhitelistUpdated();
    }

    default boolean isWhitelisted(UUID playerUID) {
        return getWhitelistData().getWhitelist().contains(playerUID);
    }

    void removeWhitelistPlayer(UUID playerUID);

    default UUID getLastWhitelisted() {
        return getWhitelistData().getLastWhitelisted();
    }

    default void setLastWhitelisted(UUID lastWhitelisted) {
        this.getWhitelistData().setLastWhitelisted(lastWhitelisted);
    }

    default boolean isWhitelistUpdated() {
        return this.getWhitelistData().isWhitelistUpdated();
    }

    default void hasWhitelistUpdated() {
        this.setWhitelistUpdated(true);
    }

    default void setWhitelistUpdated(boolean whitelistUpdated) {
        this.getWhitelistData().setWhitelistUpdated(whitelistUpdated);
    }
}