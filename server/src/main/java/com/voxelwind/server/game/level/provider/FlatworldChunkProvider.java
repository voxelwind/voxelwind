package com.voxelwind.server.game.level.provider;

import com.flowpowered.math.vector.Vector2i;
import com.voxelwind.api.game.level.Chunk;
import com.voxelwind.api.game.level.block.types.BlockTypes;
import com.voxelwind.server.game.level.block.BasicBlockState;
import com.voxelwind.server.game.level.chunk.VoxelwindChunk;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class FlatworldChunkProvider implements ChunkProvider {
    public static final FlatworldChunkProvider INSTANCE = new FlatworldChunkProvider();
    private final Map<Vector2i, VoxelwindChunk> chunks = new ConcurrentHashMap<>();

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

    private VoxelwindChunk generate(Vector2i vector2i) {
        VoxelwindChunk chunk = new VoxelwindChunk(vector2i.getX(), vector2i.getY());
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                chunk.setBlock(x, 0, z, new BasicBlockState(BlockTypes.BEDROCK, null));
                for (int y = 1; y < 4; y++) {
                    chunk.setBlock(x, y, z, new BasicBlockState(BlockTypes.DIRT, null));
                }
                chunk.setBlock(x, 4, z, new BasicBlockState(BlockTypes.GRASS_BLOCK, null));
            }
        }

        if (vector2i.equals(Vector2i.ZERO)) {
            chunk.setBlock(0, 4, 0, new BasicBlockState(BlockTypes.BEDROCK, null));
        }
        return chunk;
    }
}
