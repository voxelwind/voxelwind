package com.voxelwind.server.util;

/**
 * This class represents a rotation. Pocket Edition uses degrees to measure angles.
 */
public class Rotation {
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
