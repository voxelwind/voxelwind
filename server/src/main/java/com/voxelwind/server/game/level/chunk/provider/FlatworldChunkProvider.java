package com.voxelwind.server.game.level.chunk.provider;

import com.voxelwind.api.game.level.Chunk;
import com.voxelwind.api.game.level.Level;
import com.voxelwind.api.game.level.block.BlockTypes;
import com.voxelwind.server.game.level.block.BasicBlockState;
import com.voxelwind.server.game.level.chunk.SectionedChunk;
import com.voxelwind.server.game.level.chunk.provider.anvil.ChunkSection;

import java.util.concurrent.CompletableFuture;

public class FlatworldChunkProvider implements ChunkProvider {
    public static final FlatworldChunkProvider INSTANCE = new FlatworldChunkProvider();

    private FlatworldChunkProvider() {

    }

    @Override
    public CompletableFuture<Chunk> createChunk(Level level, int x, int z) {
        return CompletableFuture.supplyAsync(() -> {
            SectionedChunk chunk = new SectionedChunk(new ChunkSection[8], x, z, level);
            for (int x1 = 0; x1 < 16; x1++) {
                for (int z1 = 0; z1 < 16; z1++) {
                    chunk.setBlock(x1, 0, z1, new BasicBlockState(BlockTypes.BEDROCK, null, null), false);
                    for (int y = 1; y < 4; y++) {
                        chunk.setBlock(x1, y, z1, new BasicBlockState(BlockTypes.DIRT, null, null), false);
                    }
                    chunk.setBlock(x1, 4, z1, new BasicBlockState(BlockTypes.GRASS_BLOCK, null, null), false);
                }
            }

            if (x == 0 && z == 0) {
                chunk.setBlock(0, 4, 0, new BasicBlockState(BlockTypes.BEDROCK, null, null), false);
            }

            chunk.recalculateLight();
            return chunk;
        });
    }
}
