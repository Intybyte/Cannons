package at.pavlov.bukkit.container;

import at.pavlov.internal.container.SoundHolder;
import org.bukkit.Sound;

import java.util.function.Function;

public class BukkitSoundHolder extends SoundHolder<Sound> {
    public BukkitSoundHolder(String str) {
        super(str);
    }

    @Override
    public Function<String, Sound> converter() {
        return Sound::valueOf;
    }
}
