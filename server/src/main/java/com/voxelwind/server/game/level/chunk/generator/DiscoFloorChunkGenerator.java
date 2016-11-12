package com.voxelwind.server.game.level.chunk.generator;

import com.google.common.collect.ImmutableList;
import com.voxelwind.api.game.level.Chunk;
import com.voxelwind.api.game.level.Level;
import com.voxelwind.api.game.level.block.BlockState;
import com.voxelwind.api.game.level.block.BlockType;
import com.voxelwind.api.game.level.block.BlockTypes;
import com.voxelwind.server.game.level.block.BasicBlockState;

import java.util.List;
import java.util.Random;

public class DiscoFloorChunkGenerator implements ChunkGenerator {
    private static final List<BlockType> TYPES = ImmutableList.of(
            BlockTypes.COAL_BLOCK, BlockTypes.DIAMOND_BLOCK, BlockTypes.GOLD_BLOCK, BlockTypes.IRON_BLOCK,
            BlockTypes.REDSTONE_BLOCK, BlockTypes.EMERALD_BLOCK, BlockTypes.LAPIS_LAZULI_BLOCK
    );
    private static final BlockState BEDROCK = new BasicBlockState(BlockTypes.BEDROCK, null, null);

    @Override
    public void generate(Level level, Chunk chunk, Random random) {
        for (int x1 = 0; x1 < 16; x1++) {
            for (int z1 = 0; z1 < 16; z1++) {
                chunk.setBlock(x1, 0, z1, BEDROCK, false);
                BlockType type = TYPES.get(random.nextInt(TYPES.size()));
                chunk.setBlock(x1, 1, z1, new BasicBlockState(type, null, null), false);
            }
        }

        if (chunk.getX() == 0 && chunk.getZ() == 0) {
            chunk.setBlock(0, 4, 0, BEDROCK, false);
        }
    }
}
