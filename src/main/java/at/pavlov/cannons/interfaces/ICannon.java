package at.pavlov.cannons.interfaces;

import at.pavlov.cannons.interfaces.holders.CannonDataHolder;

import java.util.UUID;

public interface ICannon extends CannonDataHolder {

    boolean sameType(ICannon cannon);

    void setUID(UUID id);
    UUID getUID();

    //region Temperature Handling
    void setTemperature(double temp);
    double getTemperature();

    void setTemperatureTimeStamp(long timeStamp);
    long getTemperatureTimeStamp();
    //endregion

    void setPaid(boolean paid);
    boolean isPaid();
}
