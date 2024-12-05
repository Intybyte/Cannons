package at.pavlov.cannons.scheduler;


import at.pavlov.cannons.Cannons;
import at.pavlov.cannons.cannon.Cannon;
import at.pavlov.cannons.cannon.CannonManager;
import org.bukkit.scheduler.BukkitRunnable;

public class CreateCannon extends BukkitRunnable {

    private final Cannons plugin;
    private Cannon cannon;
    private Boolean saveToDatabase;

    public CreateCannon(Cannons plugin, Cannon cannon, boolean saveToDatabase){
        this.plugin = plugin;
        this.cannon = cannon;
        this.saveToDatabase = saveToDatabase;
    }

    @Override
    public void run() {
        CannonManager.getInstance().createCannon(cannon, saveToDatabase);
    }
}
