package at.pavlov.bukkit.container;

import at.pavlov.internal.CannonLogger;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Sound;

import java.util.Locale;
import java.util.Scanner;
import java.util.logging.Level;

@Data public class SoundHolder {
    private Sound soundEnum;
    private String soundString;
    private Float volume;
    private Float pitch;

    public SoundHolder(String str) {
        // data structure:
        // 'IRON_GOLEM_WALK:1:0.5'
        try(Scanner s = new Scanner(str).useDelimiter("\\s*:\\s*")) {
            soundEnum = null;
            soundString = null;
            volume = 1.0F;
            pitch = 1.0F;
            //Scanner s = new Scanner(str).useDelimiter("\\s*:\\s*");

            // use US locale to be able to identify floats in the string
            s.useLocale(Locale.US);

            if (s.hasNext()) {
                String scan = s.next();
                if (scan == null || scan.equalsIgnoreCase("none")) {
                    return;
                }

                try {
                    soundEnum = Sound.valueOf(scan);
                } catch (Exception e) {
                    soundString = scan;
                }
                return;
            }

            CannonLogger.getLogger().log(Level.WARNING,"missing sound value in: " + str);

            if (s.hasNextFloat()) {
                volume = s.nextFloat();
                return;
            }

            CannonLogger.getLogger().log(Level.WARNING,"missing volume value in: " + str);
            if (s.hasNextFloat()) {
                pitch = s.nextFloat();
            }
            CannonLogger.getLogger().log(Level.WARNING,"missing pitch value in: " + str);
        } catch (Exception e) {
            CannonLogger.getLogger().log(Level.SEVERE, "Error while converting " + str + ". Formatting: 'IRON_GOLEM_WALK:1:0.5'" + e.toString());
        }
    }

    public SoundHolder(Sound sound, float volume, float pitch) {
        this.soundEnum = sound;
        this.volume = volume;
        this.pitch = pitch;
    }

    public SoundHolder(String sound, float volume, float pitch) {
        this.soundString = sound;
        this.volume = volume;
        this.pitch = pitch;
    }

    public boolean isValid() {
        return this.soundEnum != null || this.soundString != null;
    }

    public boolean isSoundString() {
        return this.soundString != null;
    }

    public boolean isSoundEnum() {
        return this.soundEnum != null;
    }

    public String toString() {
        if (this.soundEnum != null)
            return this.soundEnum + ":" + volume + ":" + pitch;
        else if (this.soundString != null)
            return this.soundString + ":" + volume + ":" + pitch;
        else
            return "Sound not found";
    }
}
