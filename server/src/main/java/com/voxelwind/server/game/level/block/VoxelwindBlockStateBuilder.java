package com.voxelwind.server.game.level.block;

import com.google.common.base.Preconditions;
import com.voxelwind.api.game.Metadata;
import com.voxelwind.api.game.level.block.BlockState;
import com.voxelwind.api.game.level.block.BlockStateBuilder;
import com.voxelwind.api.game.level.block.BlockType;

import javax.annotation.Nonnull;

public class VoxelwindBlockStateBuilder implements BlockStateBuilder {
    private BlockType type;
    private Metadata metadata;

    @Override
    public BlockStateBuilder blockType(@Nonnull BlockType type) {
        Preconditions.checkNotNull(type, "type");
        this.type = type;
        this.metadata = null; // No data
        return this;
    }

    @Override
    public BlockStateBuilder data(Metadata data) {
        if (data != null) {
            Preconditions.checkState(type != null, "ItemType has not been set");
            Preconditions.checkArgument(type.getMetadataClass() != null, "Item does not have any data associated with it.");
            Preconditions.checkArgument(data.getClass().isAssignableFrom(type.getMetadataClass()), "ItemType data is not valid (wanted %s)",
                    type.getMetadataClass().getName());
        }
        this.metadata = data;
        return this;
    }

    @Override
    public BlockState build() {
        Preconditions.checkArgument(type != null, "block type has not been set");
        return new BasicBlockState(type, metadata, null);
    }
}
