package at.pavlov.internal.container;

import at.pavlov.internal.projectile.Projectile;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.Objects;
import java.util.UUID;

@Accessors(fluent = true)
@Getter
public abstract class DeathCause<Proj extends Projectile<?, ?, ?, ?, ?, ?, ?, ?, ?>> {
    protected final Proj projectile;
    protected final UUID cannonUID;
    protected final UUID shooterUID;

    public DeathCause(Proj projectile, UUID cannonUID, UUID shooterUID) {
        this.projectile = projectile;
        this.cannonUID = cannonUID;
        this.shooterUID = shooterUID;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null) return false;

        if (!(obj instanceof DeathCause<?> deathCause)) {
            return false;
        }

        return Objects.equals(this.projectile, deathCause.projectile) &&
                Objects.equals(this.cannonUID, deathCause.cannonUID) &&
                Objects.equals(this.shooterUID, deathCause.shooterUID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(projectile, cannonUID, shooterUID);
    }

    @Override
    public String toString() {
        return "DeathCause[" +
                "projectile=" + projectile + ", " +
                "cannonUID=" + cannonUID + ", " +
                "shooterUID=" + shooterUID + ']';
    }
}
