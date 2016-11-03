package com.voxelwind.server.game.serializer;

import com.voxelwind.api.game.Metadata;
import com.voxelwind.api.game.level.block.BlockState;
import com.voxelwind.api.game.level.block.BlockType;
import com.voxelwind.api.game.level.blockentities.BlockEntity;

import java.util.Optional;

/**
 * @author geNAZt
 * @version 1.0
 */
class SerializerTestBase {
    BlockState generateTestBlockState( BlockType blockType, BlockEntity blockEntity ) {
        return generateTestBlockState(blockType, blockEntity, null);
    }

    BlockState generateTestBlockState( BlockType blockType, BlockEntity blockEntity, Metadata metadata ) {
        return new BlockState() {
            @Override
            public BlockType getBlockType() {
                return blockType;
            }

            @Override
            public Metadata getBlockData() {
                return metadata;
            }

            @Override
            public Optional<BlockEntity> getBlockEntity() {
                return Optional.ofNullable(blockEntity);
            }
        };
    }
}
