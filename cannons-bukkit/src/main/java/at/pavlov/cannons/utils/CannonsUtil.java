package at.pavlov.cannons.utils;

import at.pavlov.bukkit.factory.CoordinateUtil;
import at.pavlov.bukkit.projectile.BukkitProjectile;
import at.pavlov.cannons.Cannons;
import at.pavlov.bukkit.projectile.BukkitFlyingProjectile;
import at.pavlov.internal.container.location.CannonVector;
import at.pavlov.internal.container.location.Coordinate;
import at.pavlov.internal.enums.ProjectileProperties;
import io.papermc.lib.PaperLib;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;
import java.util.logging.Level;


public class CannonsUtil
{
    private static final Random random =  new Random();
	
	
	/**
	 * rotates the direction by 90°
	 * @param face
	 * @return
	 */
	public static BlockFace roatateFace(BlockFace face) {
		if (face.equals(BlockFace.NORTH)) return BlockFace.EAST;
		if (face.equals(BlockFace.EAST)) return BlockFace.SOUTH;
		if (face.equals(BlockFace.SOUTH)) return BlockFace.WEST;
		if (face.equals(BlockFace.WEST)) return BlockFace.NORTH;
		return BlockFace.UP;
	}

    /**
     * rotates the direction by -90°
     * @param face
     * @return
     */
    public static BlockFace roatateFaceOpposite(BlockFace face)
    {
        if (face.equals(BlockFace.NORTH)) return BlockFace.WEST;
        if (face.equals(BlockFace.EAST)) return BlockFace.NORTH;
        if (face.equals(BlockFace.SOUTH)) return BlockFace.EAST;
        if (face.equals(BlockFace.WEST)) return BlockFace.SOUTH;
        return BlockFace.UP;
    }

    /**
	 * get all block next to this block (UP, DOWN, SOUT, WEST, NORTH, EAST)
	 * @param block
	 * @return
	 */
	public static ArrayList<Block> SurroundingBlocks(Block block) {
		ArrayList<Block> Blocks = new ArrayList<>();

		Blocks.add(block.getRelative(BlockFace.UP));
		Blocks.add(block.getRelative(BlockFace.DOWN));
        Blocks.addAll(HorizontalSurroundingBlocks(block));
		return Blocks;
	}

	/**
	 * get all block in the horizontal plane next to this block (SOUTH, WEST, NORTH, EAST)
	 * @param block
	 * @return
	 */
	public static ArrayList<Block> HorizontalSurroundingBlocks(Block block) {
		ArrayList<Block> Blocks = new ArrayList<>();

		Blocks.add(block.getRelative(BlockFace.SOUTH));
		Blocks.add(block.getRelative(BlockFace.WEST));
		Blocks.add(block.getRelative(BlockFace.NORTH));
		Blocks.add(block.getRelative(BlockFace.EAST));
		return Blocks;
	}
	
	
	/**
	 * returns the yaw of a given blockface
	 * @param direction
	 * @return
	 */
    public static int directionToYaw(BlockFace direction) {
        return switch (direction) {
            case NORTH -> 180;
            case EAST -> 270;
            case SOUTH -> 0;
            case WEST -> 90;
            case NORTH_EAST -> 135;
            case NORTH_WEST -> 45;
            case SOUTH_EAST -> -135;
            case SOUTH_WEST -> -45;
            default -> 0;
        };
    }

    /**
     * returns a random block face
     * @return - random BlockFace
     */
    public static BlockFace randomBlockFaceNoDown() {
        return switch (random.nextInt(5)) {
            case 0 -> BlockFace.UP;
            case 1 -> BlockFace.EAST;
            case 2 -> BlockFace.SOUTH;
            case 3 -> BlockFace.WEST;
            case 4 -> BlockFace.NORTH;
            default -> BlockFace.SELF;
        };
    }

    /**
     * adds a little bit random to the location so the effects don't spawn at the same point.
     * @return - randomized location
     */
    public static Location randomLocationOrthogonal(Location loc, BlockFace face) {

        //this is the direction we want to avoid
        Vector vect = new Vector(face.getModX(),face.getModY(),face.getModZ());
        //orthogonal vector - somehow
        vect = vect.multiply(vect).subtract(new Vector(1,1,1));

        loc.setX(loc.getX()+vect.getX()*(random.nextDouble()-0.5));
        loc.setY(loc.getY()+vect.getY()*(random.nextDouble()-0.5));
        loc.setZ(loc.getZ()+vect.getZ()*(random.nextDouble()-0.5));

        return loc;
    }


    /**
     * find the surface in the given direction
     * @param start starting point
     * @param direction direction
     * @return returns the the location of one block in front of the surface or (if the surface is not found) the start location
     */
    public static Location findSurface(Location start, Vector direction) {
        World world = start.getWorld();
        Location surface = start.clone();

        //see if there is a block already - then go back if necessary
        if (!start.getBlock().isEmpty())
            surface.subtract(direction);

        //are we now in air - if not, something is wrong
        if (!start.getBlock().isEmpty())
            return start;

        try {
            direction.checkFinite();
        } catch (Exception e) {
            return start;
        }

        //int length = (int) (direction.length()*3);
        Cannons.logSDebug("World: " + world + " Start vector: " + start.toVector() + " Normalized direction: " + direction.clone().normalize());
        BlockIterator iter = new BlockIterator(world, start.toVector(), direction.clone().normalize(), 0, 10);

        //try to find a surface of the
        while (iter.hasNext())
        {
            Block next = iter.next();
            //if there is no block, go further until we hit the surface
            if (next.isEmpty())
                surface = next.getLocation();
            else
                return surface;
        }
        // no surface found
        return surface;
    }

    /**
     * find the first block on the surface in the given direction
     * @param start starting point
     * @param direction direction
     * @return returns the the location of one block in front of the surface or (if the surface is not found) the start location
     */
    public static Location findFirstBlock(Location start, Vector direction) {
        World world = start.getWorld();
        Location surface = start.clone();

        //see if there is a block already - then go back if necessary
        if (!start.getBlock().isEmpty())
            surface.subtract(direction);

        //are we now in air - if not, something is wrong
        if (!start.getBlock().isEmpty())
            return start;

        //check if the direction is > 0, otherwise the blockiterator will fail
        if (direction.lengthSquared() < 0.01)
            return start;

        //int length = (int) (direction.length()*3);
        BlockIterator iter = new BlockIterator(world, start.toVector(), direction.clone().normalize(), 0., 10);

        //try to find a surface of the
        while (iter.hasNext())
        {
            Block next = iter.next();
            //if there is no block, go further until we hit the surface
            if (!next.isEmpty())
                return next.getLocation();
        }
        // no surface found
        return null;
    }


    /**
     * checks if the line of sight is clear
     * @param start start point
     * @param stop end point
     * @param ignoredBlocks how many solid non transparent blocks are acceptable
     * @return true if there is a line of sight
     */
    public static boolean hasLineOfSight(Location start, Location stop, int ignoredBlocks){
        Vector dir =  stop.clone().subtract(start).toVector().normalize();

        if (!start.getWorld().equals(stop.getWorld())) return false;

        // limit the iteration distance to 200 blocks
        int maxDistance = (int) start.distance(stop);
        if (maxDistance > 200) maxDistance = 200;

        BlockIterator iter = new BlockIterator(start.getWorld(), start.clone().add(dir).toVector(),dir, 0, maxDistance);

        int nontransparent = 0;
        while (iter.hasNext()) {
            Block next = iter.next();
            // search for a solid non transparent block (liquids are ignored)
            if (next.getType().isSolid() && next.getType().isOccluding()) {
                nontransparent ++;
            }
        }
        //System.out.println("non transperent blocks: " + nontransparent);
        return nontransparent <= ignoredBlocks;
    }

    /**
     * returns a random point in a sphere
     * @param center center location
     * @param radius radius of the sphere
     * @return returns a random point in a sphere
     */
    public static Location randomPointInSphere(Location center, double radius) {
        double r = radius*random.nextDouble();
        double polar = Math.PI*random.nextDouble();
        double azi = Math.PI*(random.nextDouble()*2.0-1.0);
        //sphere coordinates
        double x = r*Math.sin(polar)*Math.cos(azi);
        double y = r*Math.sin(polar)*Math.sin(azi);
        double z = r*Math.cos(polar);
        return center.clone().add(x,z,y);
    }

    /**
     * returns a random point in a sphere
     * @param center center location
     * @param radius radius of the sphere
     * @return returns a random point in a sphere
     */
    public static Coordinate randomPointInSphere(Coordinate center, double radius) {
        double r = radius*random.nextDouble();
        double polar = Math.PI*random.nextDouble();
        double azi = Math.PI*(random.nextDouble()*2.0-1.0);
        //sphere coordinates
        double x = r*Math.sin(polar)*Math.cos(azi);
        double y = r*Math.sin(polar)*Math.sin(azi);
        double z = r*Math.cos(polar);

        Coordinate coordinate = center.clone();
        coordinate.getVector().add(x,z,y);
        return coordinate;
    }


    /**
     * returns a random number in the given range
     * @param min smallest value
     * @param max largest value
     * @return a integer in the given range
     */
    public static int getRandomInt(int min, int max)
    {
        return random.nextInt(max+1-min) + min;
    }

    /**
     * teleports the player back to the starting point if the cannonball has the property 'observer'
     * @param cannonball the flying projectile
     */
    public static void teleportBack(BukkitFlyingProjectile cannonball) {
        if (cannonball == null)
            return;

        Player player = Bukkit.getPlayer(cannonball.getShooterUID());
        if (player == null)
            return;

        BukkitProjectile projectile = cannonball.getProjectile();

        Location teleLoc = null;
        //teleport the player back to the location before firing
        if(projectile.hasProperty(ProjectileProperties.OBSERVER)) {
            teleLoc = CoordinateUtil.toLoc(cannonball.getPlayerlocation());
        }
        //teleport to this location
        if (teleLoc == null) {
            return;
        }

        teleLoc.setYaw(player.getLocation().getYaw());
        teleLoc.setPitch(player.getLocation().getPitch());
        PaperLib.teleportAsync(player, teleLoc);
        player.setVelocity(new Vector(0,0,0));
        cannonball.setTeleported(true);
    }


    public static double vectorToYaw(Vector vector){
        return Math.atan2(-vector.getX(), vector.getZ())*180./Math.PI;
    }

    public static double vectorToPitch(Vector vector){
        return -Math.asin(vector.normalize().getY())*180./Math.PI;
    }

    public static CannonVector directionToVector(double yaw, double pitch, double speed){
        double hx = -Math.cos(pitch * Math.PI / 180.)*Math.sin(yaw*Math.PI/180.);
        double hy = -Math.sin(pitch * Math.PI / 180.);
        double hz = Math.cos(pitch*Math.PI/180.)*Math.cos(yaw*Math.PI/180.);
//        System.out.println("yaw: " + yaw + " pitch " + pitch);
//        System.out.println("vector: " + (new Vector(hx, hy, hz)));
        return new CannonVector(hx, hy, hz).multiply(speed);
    }

    /**
     * returns the offline player for a given player name if he played on the server
     * @param name name of the player
     * @return Offline player
     */
    public static OfflinePlayer getOfflinePlayer(String name){
        OfflinePlayer[] players = Bukkit.getOfflinePlayers();
        for (OfflinePlayer player : players) {
           if (player.getName().equals(name)) {
               return player;
           }
        }
        return null;
    }

    /**
     * returns true if the player is playing or has been on this server before
     * @param uuid id if the player
     * @return true if player has played before
     */
    public static boolean hasPlayedBefore(UUID uuid){
        OfflinePlayer bPlayer = Bukkit.getOfflinePlayer(uuid);
        if (bPlayer == null)
            return false;

        if (!bPlayer.isOnline()) {
            return bPlayer.hasPlayedBefore();
        }

        Player player = (Player) bPlayer;

        return player.isOnline();
    }

    /**
     * Find the closed block edge for the given direction and return the blockface normal.
     * @param impactLocation Location of impact above the surface
     * @param direction impact direction of the cannonball
     * @return vector normal to plane
     */
    public static Vector detectImpactSurfaceNormal(Vector impactLocation, Vector direction){
        double plane;
        //the block location
        Vector imb = new Vector(Math.round(impactLocation.getX()), Math.round(impactLocation.getY()), Math.round(impactLocation.getZ()));
        //impact vector location relative to the block
        Vector rv = impactLocation.subtract(imb);
        //Y - vertical
        if (direction.getY() > 0)
            //impact was below
            plane = 0.5;
        else
            //impact was above
            plane = -0.5;
        Cannons.logger().info("impact: " + imb + " rv: " + rv + " direction " + direction + " plane: " + plane);
        double t = (plane - rv.getY())/direction.getY();
        Vector is = direction.clone().multiply(t).add(rv);
        //detect if is within bonds
        Cannons.logger().info("isurface: " + is);
        if (is.getX() > -0.5 && is.getX() < 0.5 && is.getZ() > -0.5 && is.getZ() < 0.5){
            return new Vector(0,1,0);
        }


        //X - horizontal

        //Z - horizontal
        return new Vector (0,1,0);
    }

    /**
     * rotates the Facing of a BlockData clockwise
     * @param blockData blockData
     * @return rotated blockData
     */
    public static BlockData roateBlockFacingClockwise(BlockData blockData){
        if (blockData instanceof Directional directional){
            directional.setFacing(roatateFace(directional.getFacing()));
        }
        return blockData;
    }

    /**
     * create BlockData and checks if the result is valid
     * @param str Material name
     * @return BlockData or AIR if the block is not valid
     */
    public static BlockData createBlockData(String str){
        try{
            return Bukkit.createBlockData(str);
        }
        catch(Exception e){
            Cannons.logger().log(Level.WARNING,"block data '" + str + "' is not valid");
            return Material.AIR.createBlockData();
        }
    }
}
