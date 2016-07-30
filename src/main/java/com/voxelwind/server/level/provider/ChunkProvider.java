package com.voxelwind.server.level.provider;

import com.flowpowered.math.vector.Vector3f;
import com.voxelwind.server.level.chunk.Chunk;

import java.util.concurrent.CompletableFuture;

public interface ChunkProvider {
    CompletableFuture<Chunk> get(int x, int z);
    boolean unload(int x, int z);
    Vector3f getSpawn();
}
