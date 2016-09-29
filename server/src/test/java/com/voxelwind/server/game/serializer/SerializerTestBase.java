package com.voxelwind.server.game.serializer;

import com.flowpowered.math.vector.Vector3i;
import com.voxelwind.api.game.Metadata;
import com.voxelwind.api.game.level.Chunk;
import com.voxelwind.api.game.level.Level;
import com.voxelwind.api.game.level.block.Block;
import com.voxelwind.api.game.level.block.BlockState;
import com.voxelwind.api.game.level.block.BlockType;
import com.voxelwind.api.game.level.block.BlockTypes;
import com.voxelwind.api.game.level.blockentities.BlockEntity;

import java.util.Optional;

/**
 * @author geNAZt
 * @version 1.0
 */
class SerializerTestBase {
    BlockState generateTestBlockState( BlockType blockType, BlockEntity blockEntity ) {
        return new BlockState() {
            @Override
            public BlockType getBlockType() {
                return BlockTypes.FLOWER_POT;
            }

            @Override
            public Metadata getBlockData() {
                return null;
            }

            @Override
            public Optional<BlockEntity> getBlockEntity() {
                return Optional.ofNullable(blockEntity);
            }
        };
    }
}
