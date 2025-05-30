package at.pavlov.internal.cannon.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Data public class CannonMainData {
    // Database id - is -1 until stored in the database. Then it is the id in the
    // database
    private UUID databaseId;
    private String cannonName;
    // was the cannon fee paid
    private boolean paid;
    // player who has build this cannon
    private UUID owner;
    // designID of the cannon, for different types of cannons - not in use
    private boolean isValid;
}
