package com.voxelwind.server.game.level.provider.anvil;

import com.flowpowered.nbt.Tag;
import com.voxelwind.api.game.level.Chunk;
import com.voxelwind.api.game.level.Level;
import com.voxelwind.server.game.level.chunk.VoxelwindChunk;
import com.voxelwind.server.game.level.provider.ChunkProvider;
import lombok.Value;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by andrew on 9/24/16.
 */
public class AnvilChunkProvider implements ChunkProvider {
    private final Path basePath;
    private final Map<RegionXZ, List<Tag<?>>> regionCache = new ConcurrentHashMap<>();

    public AnvilChunkProvider(Path basePath) {
        this.basePath = basePath;
    }

    @Override
    public CompletableFuture<Chunk> createChunk(Level level, int x, int z) {
        /*RegionXZ rXZ = RegionXZ.fromChunkXZ(x, z);
        if (regionCache.containsKey(rXZ)) {

        }*/
        return null;
    }

    private Chunk complete(List<Tag<?>> tags, Level level, int x, int z) {
        System.out.println(tags);
        return new VoxelwindChunk(level, x, z);
    }

    @Value
    private static class RegionXZ {
        private final int x;
        private final int z;

        public static RegionXZ fromChunkXZ(int x, int z) {
            return new RegionXZ(x >> 5, z >> 5);
        }
    }
}
