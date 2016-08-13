package com.voxelwind.server.level.provider;

import com.flowpowered.math.vector.Vector2i;
import com.flowpowered.math.vector.Vector3f;
import com.voxelwind.server.level.chunk.Chunk;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class FlatworldChunkProvider implements ChunkProvider {
    public static final FlatworldChunkProvider INSTANCE = new FlatworldChunkProvider();
    private final Map<Vector2i, Chunk> chunks = new ConcurrentHashMap<>();

    private FlatworldChunkProvider() {

    }

    @Override
    public CompletableFuture<Chunk> get(int x, int z) {
        return CompletableFuture.completedFuture(chunks.computeIfAbsent(new Vector2i(x, z), this::generate));
    }

    @Override
    public Optional<Chunk> getIfLoaded(int x, int z) {
        return Optional.ofNullable(chunks.get(new Vector2i(x, z)));
    }

    @Override
    public boolean unload(int x, int z) {
        return chunks.remove(new Vector2i(x, z)) != null;
    }

    @Override
    public Vector3f getSpawn() {
        return new Vector3f(0, 5, 0);
    }

    private Chunk generate(Vector2i vector2i) {
        Chunk chunk = new Chunk(vector2i.getX(), vector2i.getY());
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                chunk.setBlock(x, 0, z, (byte) 7);
                for (int y = 1; y < 4; y++) {
                    chunk.setBlock(x, y, z, (byte) 3);
                }
                chunk.setBlock(x, 4, z, (byte) 2);
            }
        }
        return chunk;
    }
}
