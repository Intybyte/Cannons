package at.pavlov.internal.projectile.data;

import lombok.Data;

@Data
public class ExplosionData {
    private float explosionPower;
    private boolean explosionPowerDependsOnVelocity;
    private boolean explosionDamage;
    private boolean underwaterDamage;
    private boolean penetrationDamage;
}
