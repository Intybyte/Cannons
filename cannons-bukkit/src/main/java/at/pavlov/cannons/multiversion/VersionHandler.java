package at.pavlov.cannons.multiversion;

import org.bukkit.Bukkit;

public final class VersionHandler {
    private static int[] version;

    static {
        initVersion();
    }

    private VersionHandler() {}

    private static void initVersion() {
        var temp = Bukkit.getBukkitVersion().split("-")[0].split("\\.");
        version = new int[3];
        for (int i = 0; i < temp.length; i++) {
            version[i] = Integer.parseInt(temp[i]);
        }

        if (temp.length != 3) {
            version[2] = 0;
        }
    }

    public static int[] getVersion() {
        return version;
    }
}
