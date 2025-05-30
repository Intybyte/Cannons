package at.pavlov.cannons.API;

import at.pavlov.cannons.Cannons;
import at.pavlov.internal.enums.BreakCause;
import at.pavlov.internal.enums.InteractAction;
import at.pavlov.internal.enums.MessageEnum;
import at.pavlov.cannons.cannon.Cannon;
import at.pavlov.cannons.cannon.CannonManager;
import net.countercraft.movecraft.MovecraftLocation;
import net.countercraft.movecraft.craft.Craft;
import net.countercraft.movecraft.craft.PilotedCraft;
import net.countercraft.movecraft.craft.SubCraft;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class CannonsAPI {

    private final Cannons plugin;

    public CannonsAPI(Cannons cannons)
    {
        this.plugin = cannons;
    }

    /**
     * fires the given cannon
     * @param cannon the cannon to fire
     * @param player the player how is firing the cannon. Player:null will skip permission check.
     * @param autoreload if the cannon will autoreload from a chest after firing
     * @param consumesAmmo if true the cannon will remove ammo from attached chests
     * @return returns a MessagesEnum if the firing was successful or not
     */
    public MessageEnum fireCannon(Cannon cannon, Player player, boolean autoreload, boolean consumesAmmo, InteractAction interaction)
    {
        return plugin.getFireCannon().fire(cannon, player.getUniqueId(), autoreload, consumesAmmo, interaction);
    }

    /**
     * fires the given cannon. Default cannon design settings for players are used.
     * @param cannon the cannon to fire
     * @param player the player how is firing the cannon
     * @return returns a MessagesEnum if the firing was successful or not
     */
    public MessageEnum playerFiring(Cannon cannon, Player player, InteractAction interaction)
    {
        return plugin.getFireCannon().playerFiring(cannon, player, interaction);
    }

    /**
     * fires the given cannon. Default cannon design settings for redstone are used.
     * @param cannon the cannon to fire
     * @return returns a MessagesEnum if the firing was successful or not
     */
    public MessageEnum redstoneFiring(Cannon cannon, InteractAction interaction)
    {
        return plugin.getFireCannon().redstoneFiring(cannon, interaction);
    }

    /**
     * returns the cannon on the given location
     * @param location - location of a cannon block
     * @param playerUID - player UID searching for the cannon. If there is no cannon he will be the owner. If null no new Cannon can be created.
     * @return - null if there is no cannon, else the cannon
     */
    public Cannon getCannon(Location location, UUID playerUID)
    {
        return CannonManager.getInstance().getCannon(location, playerUID);
    }

    /**
     * returns all known cannon in a sphere around the given location
     * @param center - center of the box
     * @param sphereRadius - radius of the sphere in blocks
     * @return - list of all cannons in this sphere
     */
    public static HashSet<Cannon> getCannonsInSphere(Location center, double sphereRadius)
    {
        return CannonManager.getCannonsInSphere(center, sphereRadius);
    }

    /**
     * returns all known cannon in a box around the given location
     * @param center - center of the box
     * @param lengthX - box length in X
     * @param lengthY - box length in Y
     * @param lengthZ - box length in Z
     * @return - list of all cannons in this sphere
     */
    public static HashSet<Cannon> getCannonsInBox(Location center, double lengthX, double lengthY, double lengthZ)
    {
        return CannonManager.getCannonsInBox(center, lengthX, lengthY, lengthZ);
    }

    /**
     * returns all cannons for a list of locations - this will update all cannon locations
     * @param locations - a list of location to search for cannons
     * @param playerUID - player UID which operates the cannon
     * @param silent - no messages will be displayed if silent is true
     * @return - list of all cannons in this sphere
     */
    public HashSet<Cannon> getCannons(List<Location> locations, UUID playerUID, boolean silent)
    {
        return CannonManager.getInstance().getCannons(locations, playerUID, silent);
    }

    /**
     * returns all cannons for a list of locations - this will update all cannon locations
     * @param locations - a list of location to search for cannons
     * @param playerUID - player UID which operates the cannon
     * @return - list of all cannons in this sphere
     */
    public HashSet<Cannon> getCannons(List<Location> locations, UUID playerUID)
    {
        return CannonManager.getInstance().getCannons(locations, playerUID, true);
    }

    /**
     * In case you want to use Movecraft + Cannons
     * use this method to get all the cannons that are present on a craft
     *
     * @param craft Movecraft craft to scan for cannons
     * @return cannons presents on craft
     */
    public Set<Cannon> getCannons(Craft craft) {
        List<Location> shipLocations = new ArrayList<>();
        for (MovecraftLocation loc : craft.getHitBox()) {
            shipLocations.add(loc.toBukkit(craft.getWorld()));
        }
        return this.getCannons(shipLocations, getPlayerFromCraft(craft), true);
    }

    /**
     * This method tries to get the player that is piloting the craft, or if the craft
     * is a subcraft, the pilot of the parent craft.
     *
     * @param craft Movecraft craft to search for its pilot
     * @return UUID of the pilot
     */
    public UUID getPlayerFromCraft(Craft craft) {
        if (craft instanceof PilotedCraft pilotedCraft) {
            // If this is a piloted craft, return the pilot's UUID
            return pilotedCraft.getPilot().getUniqueId();
        }

        if (craft instanceof SubCraft subCraft) {
            // If this is a subcraft, look for a parent
            Craft parent = subCraft.getParent();
            if (parent != null) {
                // If the parent is not null, recursively check it for a UUID
                return getPlayerFromCraft(parent);
            }
        }

        // Return null if all else fails
        return null;
    }

    /**
     * returns the cannon from the storage
     * @param uid UUID of the cannon
     * @return the cannon from the storage
     */
    public static Cannon getCannon(UUID uid)
    {
        return CannonManager.getCannon(uid);
    }


    public void setCannonAngle(Cannon cannon, double horizontal, double vertical)
    {
        //plugin.getAiming().
    }

    /**
     * removes a cannon from the list
     * @param uid UID of the cannon
     * @param breakCannon the cannon will explode and all cannon blocks will drop
     * @param canExplode if the cannon can explode when loaded with gunpowder
     * @param cause the reason way the cannon was broken
     */
    public void removeCannon(UUID uid, boolean breakCannon, boolean canExplode, BreakCause cause)
    {
        CannonManager.getInstance().removeCannon(uid, breakCannon, canExplode, cause, true);
    }



}
