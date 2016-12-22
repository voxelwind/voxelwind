package com.voxelwind.api.game.level.block;

import com.voxelwind.api.game.Metadata;
import com.voxelwind.api.server.Server;

import javax.annotation.Nonnull;

/**
 * This interface specifies a builder for block states. You can access an instance of this interface using
 * {@link Server#createBlockStateBuilder()}.
 */
public interface BlockStateBuilder {
    /**
     * Specifies the block type to use.
     * @param type the block type to use
     * @return the builder, for chaining
     */
    BlockStateBuilder blockType(@Nonnull BlockType type);

    /**
     * Specifies the data to use for this block. This requires that a block type already be set.
     * @param data the material data to use
     * @return the builder, for chaining
     */
    BlockStateBuilder data(Metadata data);

    /**
     * Creates the {@link BlockState}.
     * @return the new block state
     */
    BlockState build();
}
