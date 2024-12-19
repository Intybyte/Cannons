package at.pavlov.cannons.utils;

import at.pavlov.bukkit.container.holders.BukkitSoundHolder;
import at.pavlov.cannons.dao.AsyncTaskManager;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class SoundUtils {
    /**
     * creates a imitated explosion sound
     *
     * @param loc     location of the explosion
     * @param sound   sound
     * @param maxDist maximum distance
     */
    public static void imitateSound(Location loc, BukkitSoundHolder sound, int maxDist, float maxVolume) {
        //https://forums.bukkit.org/threads/playsound-parameters-volume-and-pitch.151517/
        World w = loc.getWorld();
        //w.playSound(loc, sound.getSound(), maxVolume*16f, sound.getPitch());
        maxVolume = Math.max(0.0f, Math.min(0.95f, maxVolume));

        for (Player p : w.getPlayers()) {
            Location pl = p.getLocation();
            //readable code
            Vector v = loc.clone().subtract(pl).toVector();
            float d = (float) v.length();
            if (d > maxDist) {
                continue;
            }

            //float volume = 2.1f-(float)(d/maxDist);
            //float newPitch = sound.getPitch()/(float) Math.sqrt(d);
            float newPitch = sound.getPitch();
            //p.playSound(p.getEyeLocation().add(v.normalize().multiply(16)), sound, volume, newPitch);
            //https://bukkit.org/threads/playsound-parameters-volume-and-pitch.151517/
            float maxv = d / (1 - maxVolume) / 16f;
            maxv = Math.max(maxv, maxVolume);
            float setvol = Math.min(maxv, (float) maxDist / 16f);
            //System.out.println("distance: " + d + "maxv: " + maxv + " (float)maxDist/16f: " + (float)maxDist/16f + " setvol: " + setvol);
            if (sound.isSoundEnum())
                p.playSound(loc, sound.getSoundEnum(), setvol, newPitch);
            if (sound.isSoundString())
                p.playSound(loc, sound.getSoundString(), setvol, newPitch);
        }
    }

    /**
     * creates a imitated error sound (called when played doing something wrong)
     *
     * @param p player
     */
    public static void playErrorSound(final Player p) {
        if (p == null)
            return;

        playErrorSound(p.getLocation());
    }

    /**
     * creates a imitated error sound (called when played doing something wrong)
     *
     * @param location location of the error sound
     */
    public static void playErrorSound(final Location location) {
        if (location == null)
            return;

        var world = location.getWorld();
        if (world == null)
            return;

        world.playSound(location, Sound.BLOCK_NOTE_BLOCK_PLING, 0.25f, 0.75f);
        AsyncTaskManager.get().scheduler.runTaskLater(location, () ->
                world.playSound(location, Sound.BLOCK_NOTE_BLOCK_PLING, 0.25f, 0.1f), 3);
    }

    /**
     * play a sound effect for the player
     *
     * @param loc   location of the sound
     * @param sound type of sound (sound, volume, pitch)
     */
    public static void playSound(Location loc, BukkitSoundHolder sound) {
        if (!sound.isValid())
            return;

        if (sound.isSoundString())
            loc.getWorld().playSound(loc, sound.getSoundString(), sound.getVolume(), sound.getPitch());
        if (sound.isSoundEnum())
            loc.getWorld().playSound(loc, sound.getSoundEnum(), sound.getVolume(), sound.getPitch());
    }
}