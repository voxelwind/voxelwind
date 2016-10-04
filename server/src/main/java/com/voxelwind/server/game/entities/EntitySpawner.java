package com.voxelwind.server.game.entities;

import com.flowpowered.math.vector.Vector3f;
import com.voxelwind.api.server.Server;
import com.voxelwind.server.game.level.VoxelwindLevel;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class EntitySpawner {
    private Constructor<? extends BaseEntity> constructor;

    public EntitySpawner(Class<? extends BaseEntity> entityClass) {
        try {
            this.constructor = entityClass.getConstructor(VoxelwindLevel.class, Vector3f.class, Server.class);
            this.constructor.setAccessible(true);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    public <T> T spawnEntity(VoxelwindLevel level, Vector3f position, Server server) {
        try {
            return (T) this.constructor.newInstance(level, position, server);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }

        return null;
    }
}
