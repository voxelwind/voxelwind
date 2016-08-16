package com.voxelwind.server.level.entities;

import com.flowpowered.math.vector.Vector3f;
import com.voxelwind.server.level.VoxelwindLevel;
import com.voxelwind.server.util.Rotation;

public interface Entity {
    long getEntityId();

    VoxelwindLevel getLevel();

    Vector3f getPosition();

    Vector3f getGamePosition();

    Rotation getRotation();

    void setRotation(Rotation rotation);

    Vector3f getMotion();

    void setMotion(Vector3f motion);

    boolean isSprinting();

    void setSprinting(boolean sprinting);

    boolean isSneaking();

    void setSneaking(boolean sneaking);

    boolean isInvisible();

    void setInvisible(boolean invisible);

    boolean onTick();

    boolean isOnGround();

    void teleport(Vector3f position);

    void teleport(VoxelwindLevel level, Vector3f position);

    void teleport(VoxelwindLevel level, Vector3f position, Rotation rotation);

    void remove();

    boolean isRemoved();
}
