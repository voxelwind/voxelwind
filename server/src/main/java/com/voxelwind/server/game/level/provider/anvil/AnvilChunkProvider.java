package com.voxelwind.server.game.level.provider.anvil;

import com.flowpowered.nbt.CompoundMap;
import com.flowpowered.nbt.CompoundTag;
import com.flowpowered.nbt.Tag;
import com.flowpowered.nbt.stream.NBTInputStream;
import com.voxelwind.api.game.level.Chunk;
import com.voxelwind.api.game.level.Level;
import com.voxelwind.server.game.level.chunk.VoxelwindChunk;
import com.voxelwind.server.game.level.provider.ChunkProvider;
import com.voxelwind.server.game.level.provider.anvil.util.AnvilRegionReader;
import lombok.Value;

import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;

public class AnvilChunkProvider implements ChunkProvider {
    private final Path basePath;
    private final Map<RegionXZ, AnvilRegionReader> regionReaders = new HashMap<>();

    public AnvilChunkProvider(Path basePath) {
        this.basePath = basePath;
    }

    @Override
    public CompletableFuture<Chunk> createChunk(Level level, int x, int z) {
        // We will need to load chunks.
        RegionXZ rXZ = RegionXZ.fromChunkXZ(x, z);
        InRegionXZ irXZ = new InRegionXZ(x - rXZ.getX() * 32, z - rXZ.getZ() * 32);

        CompletableFuture<Chunk> chunkFuture = new CompletableFuture<>();
        ForkJoinPool.commonPool().execute(() -> {
            // Need to do a wide try-catch thanks to CompletableFuture.supplyAsync limitations.
            try {
                // Loading regions is not thread-safe. However, afterwards we can load chunks in a thread-safe matter (the
                // chunk loading methods are synchronized).
                AnvilRegionReader regionReader;
                synchronized (regionReaders) {
                    regionReader = regionReaders.get(rXZ);
                    if (regionReader == null) {
                        Path regionPath = basePath.resolve("region").resolve("r." + rXZ.x + "." + rXZ.z + ".mca");
                        try {
                            regionReader = new AnvilRegionReader(regionPath);
                            regionReaders.put(rXZ, regionReader);
                        } catch (NoSuchFileException e) {
                            // Doesn't exist, return empty chunk.
                            chunkFuture.complete(new VoxelwindChunk(level, x, z));
                            return;
                        }
                    }
                }

                // Now load the root tag from this chunk.
                if (regionReader.hasChunk(irXZ.x, irXZ.z)) {
                    Tag<?> tag;
                    try (NBTInputStream stream = new NBTInputStream(regionReader.readChunk(irXZ.x, irXZ.z), false)) {
                        tag = stream.readTag();
                    }

                    // Grab the sections.
                    CompoundMap map = ((CompoundTag) tag).getValue();
                    CompoundMap levelMap = ((CompoundMap) map.get("Level").getValue());

                    // Convert the Anvil chunk into a Voxelwind-friendly format.
                    chunkFuture.complete(AnvilConversion.convertChunkToVoxelwind(levelMap, level));
                } else {
                    // Doesn't exist, return empty chunk.
                    chunkFuture.complete(new VoxelwindChunk(level, x, z));
                }
            } catch (Exception e) {
                chunkFuture.completeExceptionally(e);
            }
        });
        return chunkFuture;
    }

    @Value
    private static class RegionXZ {
        private final int x;
        private final int z;

        public static RegionXZ fromChunkXZ(int x, int z) {
            return new RegionXZ(x >> 5, z >> 5);
        }
    }

    @Value
    private static class InRegionXZ {
        private final int x;
        private final int z;
    }
}
