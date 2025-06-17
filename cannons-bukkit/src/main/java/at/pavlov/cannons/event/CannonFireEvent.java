package at.pavlov.cannons.event;

import at.pavlov.cannons.Enum.ProjectileCause;
import at.pavlov.cannons.cannon.Cannon;
import at.pavlov.cannons.dao.wrappers.BaseFireTask;
import at.pavlov.cannons.dao.wrappers.FireTaskCreator;
import at.pavlov.cannons.dao.wrappers.FireTaskWrapper;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.UUID;


/**
 * If you want to replace cannon's firing with a custom firing mechanic, I suggest using
 * the builtin FireTaskCreator instead of cancelling the event, as this is a more reliable
 * and respects the cannon's configuration for delays etc...
 */
@Getter
@Setter
public class CannonFireEvent extends Event implements Cancellable {
    private static final @NotNull HandlerList handlers = new HandlerList();
    private final Cannon cannon;
    private final UUID player;
    private final ArrayList<FireTaskCreator> fireTaskCreators = new ArrayList<>();
    private boolean cancelled;

    public CannonFireEvent(Cannon cannon, UUID player) {
        this.cannon = cannon;
        this.player = player;
        this.cancelled = false;
    }

    //replace this with a list
    //or fully implement decorator with wrapFireTaskCreator(Function<FireTaskCreator, FireTaskCreator>) current -> next
    /**
     * Add a FireTaskCreator as a wrapper, the first to return no null is the one handled
     *
     * @param creator added FireTaskCreator
     */
    public void addFireTaskCreator(FireTaskCreator creator) {
        this.fireTaskCreators.add(creator);
    }

    public BaseFireTask createTask(Cannon cannon, UUID shooter, boolean removeCharge, ProjectileCause cause) {
        for (FireTaskCreator creator : fireTaskCreators) {
            BaseFireTask result = creator.create(cannon, shooter, removeCharge, cause);
            if (result != null) {
                return result;
            }
        }

        return new FireTaskWrapper(cannon, shooter, removeCharge, cause);
    }

    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
