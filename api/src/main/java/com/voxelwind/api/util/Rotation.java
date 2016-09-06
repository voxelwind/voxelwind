package com.voxelwind.api.util;

import com.flowpowered.math.vector.Vector3f;
import com.google.common.base.Preconditions;
import lombok.Builder;

/**
 * This class represents a rotation. Pocket Edition uses degrees to measure angles. This class is immutable.
 */
@Builder
public final class Rotation {
    public static final Rotation ZERO = new Rotation(0f, 0f, 0f);

    private final float pitch;
    private final float yaw;
    private final float headYaw;

    public Rotation(float pitch, float yaw, float headYaw) {
        this.pitch = pitch;
        this.yaw = yaw;
        this.headYaw = headYaw;
    }

    public float getPitch() {
        return pitch;
    }

    public float getYaw() {
        return yaw;
    }

    public float getHeadYaw() {
        return headYaw;
    }

    /**
     * Returns the values contained in this object as a {@link Vector3f}. The X value will be the pitch, the Y value
     * is the yaw, and the Z value will be the head yaw.
     * @return a {@link Vector3f} instance
     */
    public Vector3f toVector3f() {
        return new Vector3f(pitch, yaw, headYaw);
    }

    /**
     * Copies the value from a specified {@link Vector3f} instance into a {@link Rotation} instance.
     * @param vector3f the vector to use
     * @return the Rotation instance
     */
    public static Rotation fromVector3f(Vector3f vector3f) {
        Preconditions.checkNotNull(vector3f, "vector3f");
        return new Rotation(vector3f.getX(), vector3f.getY(), vector3f.getZ());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Rotation rotation = (Rotation) o;

        if (Float.compare(rotation.pitch, pitch) != 0) return false;
        if (Float.compare(rotation.yaw, yaw) != 0) return false;
        return Float.compare(rotation.headYaw, headYaw) == 0;

    }

    @Override
    public int hashCode() {
        int result = (pitch != +0.0f ? Float.floatToIntBits(pitch) : 0);
        result = 31 * result + (yaw != +0.0f ? Float.floatToIntBits(yaw) : 0);
        result = 31 * result + (headYaw != +0.0f ? Float.floatToIntBits(headYaw) : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Rotation{" +
                "pitch=" + pitch +
                ", yaw=" + yaw +
                ", headYaw=" + headYaw +
                '}';
    }
}
