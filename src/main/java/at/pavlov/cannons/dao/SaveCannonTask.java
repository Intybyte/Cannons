package at.pavlov.cannons.dao;

import at.pavlov.cannons.Cannons;
import at.pavlov.cannons.cannon.Cannon;
import at.pavlov.cannons.cannon.CannonManager;
import at.pavlov.cannons.projectile.Projectile;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.UUID;

public class SaveCannonTask extends BukkitRunnable {

    private UUID cannonId = null;
    public SaveCannonTask(UUID cannonId){
        this.cannonId = cannonId;
    }

    public SaveCannonTask(){
        this.cannonId = null;
    }

    @Override
    public void run() {
        String insert = String.format("REPLACE INTO '%s' " +
                "(id, name, owner, world, cannon_direction, loc_x, loc_y, loc_Z, soot, gunpowder, projectile_id, projectile_pushed, cannon_temperature, cannon_temperature_timestamp, horizontal_angle, vertical_angle, design_id, fired_cannonballs, target_mob, target_player, target_cannon, paid) VALUES" +
                "(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)"
                , Cannons.getPlugin().getCannonDatabase());
        try (PreparedStatement preparedStatement = Cannons.getPlugin().getConnection().prepareStatement(insert)) {
            for (Cannon cannon : CannonManager.getCannonList().values()) {
                // in case we want to save just one cannon
                if (this.cannonId != null && cannon.getUID() != this.cannonId) {
                    Cannons.getPlugin().logDebug("Skip entry for save in database");
                    continue;
                }

                //don't save cannons which are not valid anymore
                if (!cannon.isValid())
                    continue;




                if (cannon.getOwner() == null) {
                    Cannons.getPlugin().logDebug("Cannon not saved. Owner of cannon was null");
                    continue;
                }


                // fill the preparedStatement with values to store
                // since bukkit manages the preparedStatement, we do not need to set
                // the ID property
                preparedStatement.setString(1, cannon.getUID().toString());
                preparedStatement.setString(2, cannon.getCannonName());
                preparedStatement.setString(3, cannon.getOwner().toString());
                preparedStatement.setString(4, cannon.getWorld().toString());
                // cannon direction
                preparedStatement.setString(5,cannon.getCannonDirection().toString());
                // save offset
                preparedStatement.setInt(6,cannon.getOffset().getBlockX());
                preparedStatement.setInt(7,cannon.getOffset().getBlockY());
                preparedStatement.setInt(8,cannon.getOffset().getBlockZ());
                // must the barrel be clean with the ramrod
                preparedStatement.setDouble(9,cannon.getSoot());
                // amount of gunpowder
                preparedStatement.setInt(10,cannon.getLoadedGunpowder());

                // load projectile
                // if no projectile is found, set it to air
                Projectile projectile = cannon.getLoadedProjectile();
                if (projectile != null) {
                    preparedStatement.setString(11,projectile.getProjectileId());
                } else {
                    preparedStatement.setString(11,"none");
                }
                //is the projectile already pushed in the barrel
                preparedStatement.setInt(12,cannon.getProjectilePushed());
                //temperature
                preparedStatement.setDouble(13,cannon.getTemperature(false));
                preparedStatement.setLong(14,cannon.getTemperatureTimeStamp());

                // angles
                preparedStatement.setDouble(15,cannon.getHorizontalAngle());
                preparedStatement.setDouble(16,cannon.getVerticalAngle());
                // id
                preparedStatement.setString(17,cannon.getDesignID());

                //load fired cannonballs
                preparedStatement.setLong(18,cannon.getFiredCannonballs());

                //save targets
                preparedStatement.setBoolean(19, cannon.isTargetMob());
                preparedStatement.setBoolean(20,cannon.isTargetPlayer());
                preparedStatement.setBoolean(21,cannon.isTargetCannon());

                //save paid fee
                preparedStatement.setBoolean(22,cannon.isPaid());

                preparedStatement.addBatch();

            }
            preparedStatement.executeBatch();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //todo whitelist

//        insert = String.format("REPLACE INTO '%s' " +
//                        "(cannon_bean_id, playerid) VALUES" +
//                        "(?,?)"
//                , Cannons.getPlugin().getWhitelistDatabase());
//        try (PreparedStatement preparedStatement = Cannons.getPlugin().getConnection().prepareStatement(insert)) {
//            for (Cannon cannon : CannonManager.getCannonList().values()) {
//                for (UUID player : cannon.getWhitelist()){
//                    preparedStatement.setString(1,cannon.getUID().toString());
//                    preparedStatement.setString(2,player.toString());
//                }
//            }
//            preparedStatement.executeBatch();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }
}
