package at.pavlov.cannons.hooks.windfarer.datatag;

import at.pavlov.cannons.Cannons;
import at.pavlov.cannons.Enum.BreakCause;
import at.pavlov.cannons.Enum.CannonRotation;
import at.pavlov.cannons.cannon.Cannon;
import at.pavlov.cannons.cannon.CannonDesign;
import at.pavlov.cannons.cannon.CannonManager;
import at.pavlov.cannons.hooks.CraftCannonFinderHelper;
import at.pavlov.cannons.hooks.windfarer.DataTagKeys;
import at.pavlov.cannons.hooks.windfarer.WindfarerUtils;
import at.pavlov.cannons.hooks.windfarer.properties.CannonCraftTypeProperties;
import at.pavlov.cannons.hooks.windfarer.properties.CannonTypeConstraint;
import net.countercraft.movecraft.MovecraftLocation;
import net.countercraft.movecraft.MovecraftRotation;
import net.countercraft.movecraft.craft.Craft;
import net.countercraft.movecraft.craft.PlayerCraft;
import net.countercraft.movecraft.craft.type.TypeSafeCraftType;
import net.countercraft.movecraft.util.hitboxes.BitmapHitBox;
import net.countercraft.movecraft.util.hitboxes.HitBox;
import net.countercraft.movecraft.util.hitboxes.MutableHitBox;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class CraftCannonsData {

    // Search happens multithreaded, thus the concurrent set
    protected Set<Cannon> cannons = ConcurrentHashMap.newKeySet();
    // Attention: This field is null after validation has finished!
    // TODO: Perhaps switch to FastUtil if this has a threadsafe version?
    // TODO: If possible, switch back to a 3d bitmap as that consumes way less memory
    protected BitmapHitBox cannonBlocks = new BitmapHitBox();

    public static @NotNull CraftCannonsData of(Craft craft) {
        return craft.getDataTag(DataTagKeys.CANNONS);
    }

    // Attempts to register a cannon at that location, if there is one
    // Warning: This gets called from multiple worker threads!
    public boolean findCannonsAboardCraft(@NotNull Craft craft) {
        if (!(craft instanceof PlayerCraft)) {
            return false;
        }

        this.cannons = CraftCannonFinderHelper.findCannonsAboardCraft(craft, () -> ConcurrentHashMap.newKeySet(), this.cannonBlocks);

        return cannons.size() > 0;
    }

    public boolean validateCannons(Consumer<String> setFailMessage, Craft craft) {
        // Validate all cannons now
        // No cannons? No problem, behave like Movecraft Cannons, which doesnt validate min here!
        if (this.cannons.isEmpty() || this.cannonBlocks.isEmpty()) {
            return true;
        }

        final TypeSafeCraftType craftType = WindfarerUtils.getCraftProperties(craft);

        if (!checkCannonConstraints(setFailMessage, craftType, craft)) {
            return false;
        }
        if (!checkCannonMass(setFailMessage, craftType)) {
            return false;
        }

        // Finally, tell the cannons they are on a ship(?)
        if (craftType.get(CannonCraftTypeProperties.USE_SHIP_ANGLES)) {
            this.cannons.forEach(c -> c.setOnShip(true));
        }

        // Finally, ensure all cannons are aboard
        final HitBox craftHitBox = craft.getHitBox();
        if (craftHitBox instanceof MutableHitBox mutableHitBox) {
            mutableHitBox.addAll(this.cannonBlocks);
            craft.setOrigBlockCount(mutableHitBox.size());
        } else {
            BitmapHitBox newHitBox = new BitmapHitBox(craftHitBox);
            newHitBox.addAll(this.cannonBlocks);
            craft.setOrigBlockCount(newHitBox.size());
            craft.setHitBox(newHitBox);
        }

        this.cannonBlocks = null;

        return true;
    }

    private boolean checkCannonMass(Consumer<String> setFailMessage, TypeSafeCraftType craftType) {
        int mass = 0;
        final List<String> excludedTypes = craftType.get(CannonCraftTypeProperties.EXCLUDE_FROM_MASS);
        for (Cannon cannon : this.cannons) {
            CannonDesign design = cannon.getCannonDesign();

            if (!excludedTypes.contains(design.getDesignID())) {
                mass += design.getMassOfCannon();
            }
        }

        Cannons.getPlugin().logDebug("MassCount " + mass);

        if (craftType.hasInSelfOrAnyParent(CannonCraftTypeProperties.MAX_CANNON_MASS, true)) {
            final int maxMass = craftType.get(CannonCraftTypeProperties.MAX_CANNON_MASS);
            if (maxMass < mass) {
                setFailMessage.accept(
                        String.format(
                                "Detection Failed! Too much cannon mass on board! %d > %d", mass, maxMass
                        )
                );
                return false;
            }
        }

        if (craftType.hasInSelfOrAnyParent(CannonCraftTypeProperties.MIN_CANNON_MASS, true)) {
            final int minMass = craftType.get(CannonCraftTypeProperties.MIN_CANNON_MASS);
            if (minMass > mass) {
                setFailMessage.accept(
                        String.format(
                                "Detection Failed! Not enough cannon mass on board! %d < %d", mass, minMass
                        )
                );
                return false;
            }
        }

        return true;
    }

    private boolean checkCannonConstraints(Consumer<String> setFailMessage, TypeSafeCraftType craftType, Craft craft) {
        final List<CannonTypeConstraint> constraints = craftType.get(CannonCraftTypeProperties.CANNON_TYPE_CONSTRAINTS);
        if (constraints.isEmpty())
            return true;

        Map<String, Integer> cannonCount = new HashMap<>();
        for (Cannon cannon : cannons) {
            String design = cannon.getCannonDesign().getDesignID();
            cannonCount.compute(design, (key, value) -> (value == null) ? 1 : value + 1);
        }

        for (var entry : cannonCount.entrySet()) {
            Cannons.getPlugin().logDebug("Cannon found: " + entry.getKey() + " | " + entry.getValue());
        }

        for (CannonTypeConstraint check : constraints) {
            Optional<String> result = check.check(craft, cannonCount);

            if (result.isEmpty()) continue;

            String error = result.get();
            setFailMessage.accept("Detection Failed! " + error);
            return false;
        }

        return true;
    }

    public void onRelease() {
        this.cannons.forEach(c -> c.setOnShip(false));
    }

    public void onSunk() {
        this.cannons.forEach(
                cannon -> {
                    CannonManager.getInstance().removeCannon(cannon.getUID(), false, true, BreakCause.Explosion);
                }
        );
    }

    public void processRotation(MovecraftRotation rotation, MovecraftLocation rotationOrigin, @NotNull Craft craft) {
        final Vector v = rotationOrigin.toBukkit(craft.getWorld()).toVector();
        final CannonRotation cannonRotation = rotation == MovecraftRotation.CLOCKWISE ? CannonRotation.RIGHT : CannonRotation.LEFT;
        this.cannons.forEach(cannon -> cannon.rotate(v, cannonRotation));
    }

    public void processTranslation(int dx, int dy, int dz, UUID oldWorld, @NotNull Craft craft) {
        UUID newWorldId = craft.getWorld().getUID();
        final boolean switchedWorld = !newWorldId.equals(oldWorld);

        final Vector delta = new Vector(dx, dy, dz);
        this.cannons.forEach(cannon -> {
            cannon.move(delta);
            if (switchedWorld) {
                cannon.setWorld(newWorldId);
            }
        });
    }

    public Set<Cannon> getCannons() {
        return new HashSet<>(this.cannons);
    }

    public int getCannonCount() {
        return this.cannons.size();
    }
}
