package at.pavlov.internal.container.location;

import at.pavlov.internal.utils.NumberTricks;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;

/**
 * Class used to specify a location without needing the specific world
 * It is a very close copy to bukkit.util.Vector
 */
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@Data public class CannonVector implements Cloneable {
    private double x = 0.0, y = 0.0, z = 0.0;

    @Override
    public CannonVector clone() {
        try {
            return (CannonVector) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }


    @NotNull
    public CannonVector add(@NotNull CannonVector vec) {
        this.x += vec.x;
        this.y += vec.y;
        this.z += vec.z;
        return this;
    }

    @NotNull
    public CannonVector subtract(@NotNull CannonVector vec) {
        this.x -= vec.x;
        this.y -= vec.y;
        this.z -= vec.z;
        return this;
    }

    @NotNull
    public CannonVector multiply(@NotNull CannonVector vec) {
        this.x *= vec.x;
        this.y *= vec.y;
        this.z *= vec.z;
        return this;
    }

    @NotNull
    public CannonVector divide(@NotNull CannonVector vec) {
        this.x /= vec.x;
        this.y /= vec.y;
        this.z /= vec.z;
        return this;
    }

    @NotNull
    public CannonVector copy(@NotNull CannonVector vec) {
        this.x = vec.x;
        this.y = vec.y;
        this.z = vec.z;
        return this;
    }

    public double length() {
        return Math.sqrt(NumberTricks.square(this.x) + NumberTricks.square(this.y) + NumberTricks.square(this.z));
    }

    public double lengthSquared() {
        return NumberTricks.square(this.x) + NumberTricks.square(this.y) + NumberTricks.square(this.z);
    }

    public double distance(@NotNull CannonVector o) {
        return Math.sqrt(NumberTricks.square(this.x - o.x) + NumberTricks.square(this.y - o.y) + NumberTricks.square(this.z - o.z));
    }

    public double distanceSquared(@NotNull CannonVector o) {
        return NumberTricks.square(this.x - o.x) + NumberTricks.square(this.y - o.y) + NumberTricks.square(this.z - o.z);
    }

    public float angle(@NotNull CannonVector other) {
        double dot = NumberTricks.constrainToRange(this.dot(other) / (this.length() * other.length()), -1.0, 1.0);
        return (float)Math.acos(dot);
    }

    @NotNull
    public CannonVector midpoint(@NotNull CannonVector other) {
        this.x = (this.x + other.x) / 2.0;
        this.y = (this.y + other.y) / 2.0;
        this.z = (this.z + other.z) / 2.0;
        return this;
    }

    @NotNull
    public CannonVector getMidpoint(@NotNull CannonVector other) {
        double x = (this.x + other.x) / 2.0;
        double y = (this.y + other.y) / 2.0;
        double z = (this.z + other.z) / 2.0;
        return new CannonVector(x, y, z);
    }

    @NotNull
    public CannonVector multiply(int m) {
        this.x *= (double)m;
        this.y *= (double)m;
        this.z *= (double)m;
        return this;
    }

    @NotNull
    public CannonVector multiply(double m) {
        this.x *= m;
        this.y *= m;
        this.z *= m;
        return this;
    }

    @NotNull
    public CannonVector multiply(float m) {
        this.x *= (double)m;
        this.y *= (double)m;
        this.z *= (double)m;
        return this;
    }

    public double dot(@NotNull CannonVector other) {
        return this.x * other.x + this.y * other.y + this.z * other.z;
    }

    @NotNull
    public CannonVector crossProduct(@NotNull CannonVector o) {
        double newX = this.y * o.z - o.y * this.z;
        double newY = this.z * o.x - o.z * this.x;
        double newZ = this.x * o.y - o.x * this.y;
        this.x = newX;
        this.y = newY;
        this.z = newZ;
        return this;
    }

    @NotNull
    public CannonVector getCrossProduct(@NotNull CannonVector o) {
        double x = this.y * o.z - o.y * this.z;
        double y = this.z * o.x - o.z * this.x;
        double z = this.x * o.y - o.x * this.y;
        return new CannonVector(x, y, z);
    }

    @NotNull
    public CannonVector normalize() {
        double length = this.length();
        this.x /= length;
        this.y /= length;
        this.z /= length;
        return this;
    }

    @NotNull
    public CannonVector zero() {
        this.x = 0.0;
        this.y = 0.0;
        this.z = 0.0;
        return this;
    }

    public boolean isZero() {
        return this.x == 0.0 && this.y == 0.0 && this.z == 0.0;
    }

    @NotNull
    CannonVector normalizeZeros() {
        if (this.x == -0.0) {
            this.x = 0.0;
        }

        if (this.y == -0.0) {
            this.y = 0.0;
        }

        if (this.z == -0.0) {
            this.z = 0.0;
        }

        return this;
    }

    public boolean isInAABB(@NotNull CannonVector min, @NotNull CannonVector max) {
        return this.x >= min.x && this.x <= max.x && this.y >= min.y && this.y <= max.y && this.z >= min.z && this.z <= max.z;
    }

    public boolean isInSphere(@NotNull CannonVector origin, double radius) {
        return NumberTricks.square(origin.x - this.x) + NumberTricks.square(origin.y - this.y) + NumberTricks.square(origin.z - this.z) <= NumberTricks.square(radius);
    }

    public boolean isNormalized() {
        return Math.abs(this.lengthSquared() - 1.0) < getEpsilon();
    }

    public static double getEpsilon() {
        return 1.0E-6;
    }

    @NotNull
    public CannonVector rotateAroundX(double angle) {
        double angleCos = Math.cos(angle);
        double angleSin = Math.sin(angle);
        double y = angleCos * this.getY() - angleSin * this.getZ();
        double z = angleSin * this.getY() + angleCos * this.getZ();
        return this.setY(y).setZ(z);
    }

    @NotNull
    public CannonVector rotateAroundY(double angle) {
        double angleCos = Math.cos(angle);
        double angleSin = Math.sin(angle);
        double x = angleCos * this.getX() + angleSin * this.getZ();
        double z = -angleSin * this.getX() + angleCos * this.getZ();
        return this.setX(x).setZ(z);
    }

    @NotNull
    public CannonVector rotateAroundZ(double angle) {
        double angleCos = Math.cos(angle);
        double angleSin = Math.sin(angle);
        double x = angleCos * this.getX() - angleSin * this.getY();
        double y = angleSin * this.getX() + angleCos * this.getY();
        return this.setX(x).setY(y);
    }

    @NotNull
    public CannonVector rotateAroundAxis(@NotNull CannonVector axis, double angle) throws IllegalArgumentException {
        return this.rotateAroundNonUnitAxis(axis.isNormalized() ? axis : axis.clone().normalize(), angle);
    }

    @NotNull
    public CannonVector rotateAroundNonUnitAxis(@NotNull CannonVector axis, double angle) throws IllegalArgumentException {
        double x = this.getX();
        double y = this.getY();
        double z = this.getZ();
        double x2 = axis.getX();
        double y2 = axis.getY();
        double z2 = axis.getZ();
        double cosTheta = Math.cos(angle);
        double sinTheta = Math.sin(angle);
        double dotProduct = this.dot(axis);
        double xPrime = x2 * dotProduct * (1.0 - cosTheta) + x * cosTheta + (-z2 * y + y2 * z) * sinTheta;
        double yPrime = y2 * dotProduct * (1.0 - cosTheta) + y * cosTheta + (z2 * x - x2 * z) * sinTheta;
        double zPrime = z2 * dotProduct * (1.0 - cosTheta) + z * cosTheta + (-y2 * x + x2 * y) * sinTheta;
        return this.setX(xPrime).setY(yPrime).setZ(zPrime);
    }

    public int getBlockX() {
        return NumberTricks.floor(this.x);
    }

    public int getBlockY() {
        return NumberTricks.floor(this.y);
    }

    public int getBlockZ() {
        return NumberTricks.floor(this.z);
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof CannonVector other)) {
            return false;
        } else {
            return Math.abs(this.x - other.x) < 1.0E-6 && Math.abs(this.y - other.y) < 1.0E-6 && Math.abs(this.z - other.z) < 1.0E-6 && this.getClass().equals(obj.getClass());
        }
    }

    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + Long.hashCode(Double.doubleToLongBits(this.x));
        hash = 79 * hash + Long.hashCode(Double.doubleToLongBits(this.y));
        hash = 79 * hash + Long.hashCode(Double.doubleToLongBits(this.z));
        return hash;
    }
}
