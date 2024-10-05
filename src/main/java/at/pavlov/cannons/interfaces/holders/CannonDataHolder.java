package at.pavlov.cannons.interfaces.holders;

import at.pavlov.cannons.cannon.data.AimingData;
import at.pavlov.cannons.cannon.data.FiringData;

public interface CannonDataHolder {

    FiringData getFiringData();

    AimingData getAimingData();

}
