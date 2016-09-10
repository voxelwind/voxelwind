package com.voxelwind.server.game.level.provider;

import com.flowpowered.math.vector.Vector2i;
import com.voxelwind.api.game.level.Chunk;
import com.voxelwind.api.game.level.Level;
import com.voxelwind.api.game.level.block.BlockTypes;
import com.voxelwind.server.game.level.block.BasicBlockState;
import com.voxelwind.server.game.level.chunk.VoxelwindChunk;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class FlatworldChunkProvider implements ChunkProvider {
    public static final FlatworldChunkProvider INSTANCE = new FlatworldChunkProvider();

    private FlatworldChunkProvider() {

    }

    @Override
    public CompletableFuture<Chunk> createChunk(Level level, int x, int z) {
        return CompletableFuture.supplyAsync(() -> {
            VoxelwindChunk chunk = new VoxelwindChunk(level, x, z);
            for (int x1 = 0; x1 < 16; x1++) {
                for (int z1 = 0; z1 < 16; z1++) {
                    chunk.setBlock(x1, 0, z1, new BasicBlockState(BlockTypes.BEDROCK, null));
                    for (int y = 1; y < 4; y++) {
                        chunk.setBlock(x1, y, z1, new BasicBlockState(BlockTypes.DIRT, null));
                    }
                    chunk.setBlock(x1, 4, z1, new BasicBlockState(BlockTypes.GRASS_BLOCK, null));
                }
            }

            if (x == 0 && z == 0) {
                chunk.setBlock(0, 4, 0, new BasicBlockState(BlockTypes.BEDROCK, null));
            }
            return chunk;
        });
    }
}
