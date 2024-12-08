package at.pavlov.cannons.scheduler;


import at.pavlov.cannons.Cannons;
import at.pavlov.cannons.cannon.Cannon;
import at.pavlov.cannons.cannon.CannonManager;

public class CreateCannon implements Runnable {

    private Cannon cannon;
    private Boolean saveToDatabase;

    public CreateCannon(Cannons plugin, Cannon cannon, boolean saveToDatabase){
        this.cannon = cannon;
        this.saveToDatabase = saveToDatabase;
    }

    @Override
    public void run() {
        CannonManager.getInstance().createCannon(cannon, saveToDatabase);
    }
}
