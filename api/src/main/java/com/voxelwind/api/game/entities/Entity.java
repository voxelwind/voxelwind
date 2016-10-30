package com.voxelwind.api.game.entities;

import com.flowpowered.math.vector.Vector3f;
import com.flowpowered.math.vector.Vector3i;
import com.voxelwind.api.game.entities.components.Component;
import com.voxelwind.api.game.entities.components.system.System;
import com.voxelwind.api.game.level.Chunk;
import com.voxelwind.api.game.level.Level;
import com.voxelwind.api.game.level.block.BlockTypes;
import com.voxelwind.api.server.Server;
import com.voxelwind.api.util.Rotation;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@ParametersAreNonnullByDefault
public interface Entity {
    long getEntityId();

    @Nonnull
    Level getLevel();

    @Nonnull
    Vector3f getPosition();

    @Nonnull
    Vector3f getGamePosition();

    @Nonnull
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

    Set<Class<? extends Component>> providedComponents();

    <C extends Component> Optional<C> getComponent(Class<C> clazz);

    List<System> registeredSystems();

    void registerSystem(System system);

    void deregisterSystem(System system);

    default boolean isOnGround() {
        Vector3i blockPosition = getPosition().sub(0f, 0.1f, 0f).toInt();

        if (blockPosition.getY() < 0) {
            return false;
        }

        int chunkX = blockPosition.getX() >> 4;
        int chunkZ = blockPosition.getZ() >> 4;
        int chunkInX = blockPosition.getX() & 0x0f;
        int chunkInZ = blockPosition.getZ() & 0x0f;

        Optional<Chunk> chunkOptional = getLevel().getChunkIfLoaded(chunkX, chunkZ);
        return chunkOptional.isPresent() && chunkOptional.get().getBlock(chunkInX, blockPosition.getY(), chunkInZ).getBlockState().getBlockType() != BlockTypes.AIR;
    }

    void teleport(Vector3f position);

    void teleport(Level level, Vector3f position);

    void teleport(Level level, Vector3f position, Rotation rotation);

    void remove();

    boolean isRemoved();

    default Vector3f getDirectionVector() {
        Rotation rotation = getRotation();
        double y = -Math.sin(Math.toRadians(rotation.getPitch()));
        double xz = Math.cos(Math.toRadians(rotation.getPitch()));
        double x = -xz * Math.sin(Math.toRadians(rotation.getYaw()));
        double z = xz * Math.cos(Math.toRadians(rotation.getYaw()));
        return new Vector3f(x, y, z).normalize();
    }

    Server getServer();
}
