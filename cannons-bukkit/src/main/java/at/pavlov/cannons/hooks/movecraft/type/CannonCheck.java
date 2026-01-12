package at.pavlov.cannons.hooks.movecraft.type;

import net.countercraft.movecraft.craft.Craft;

import java.util.Map;
import java.util.Optional;

public interface CannonCheck {
    Optional<String> check(Craft craft, Map<String, Integer> cannonCountMap);
}
