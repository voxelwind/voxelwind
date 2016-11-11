package com.voxelwind.server.game.level.chunk.generator;

import com.voxelwind.api.game.level.Chunk;
import com.voxelwind.api.game.level.Level;
import com.voxelwind.api.game.level.block.BlockState;
import com.voxelwind.api.game.level.block.BlockTypes;
import com.voxelwind.server.game.level.block.BasicBlockState;

import java.util.Random;

public class SimpleFlatworldChunkGenerator implements ChunkGenerator {
    private static final BlockState BEDROCK = new BasicBlockState(BlockTypes.BEDROCK, null, null);
    private static final BlockState DIRT = new BasicBlockState(BlockTypes.DIRT, null, null);
    private static final BlockState GRASS = new BasicBlockState(BlockTypes.GRASS_BLOCK, null, null);

    @Override
    public void generate(Level level, Chunk chunk, Random random) {
        for (int x1 = 0; x1 < 16; x1++) {
            for (int z1 = 0; z1 < 16; z1++) {
                chunk.setBlock(x1, 0, z1, BEDROCK, false);
                for (int y = 1; y < 4; y++) {
                    chunk.setBlock(x1, y, z1, DIRT, false);
                }
                chunk.setBlock(x1, 4, z1, GRASS, false);
            }
        }

        if (chunk.getX() == 0 && chunk.getZ() == 0) {
            chunk.setBlock(0, 4, 0, new BasicBlockState(BlockTypes.BEDROCK, null, null), false);
        }
    }
}
