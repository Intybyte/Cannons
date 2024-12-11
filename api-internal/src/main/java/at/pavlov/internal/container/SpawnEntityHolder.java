package at.pavlov.internal.container;

import at.pavlov.internal.enums.EntityDataType;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data abstract public class SpawnEntityHolder<Entity, Potion> {
    protected Entity type;
    protected int minAmount;
    protected int maxAmount;
    protected Map<EntityDataType, String> data;
    protected List<Potion> potionEffects;
}
