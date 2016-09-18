package com.voxelwind.api.server.event.block;

import com.google.common.base.Preconditions;
import com.voxelwind.api.game.entities.Entity;
import com.voxelwind.api.game.level.block.Block;
import com.voxelwind.api.game.level.block.BlockState;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class BlockReplaceEvent implements BlockEvent {
    private final Block currentBlock;
    private final BlockState previousBlockState;
    private BlockState newBlockState;
    private final Entity source;
    private final ReplaceReason reason;

    public BlockReplaceEvent(Block currentBlock, BlockState previousBlockState, BlockState newBlockState, Entity source, ReplaceReason reason) {
        this.currentBlock = Preconditions.checkNotNull(currentBlock, "currentBlock");
        this.previousBlockState = Preconditions.checkNotNull(previousBlockState, "previousBlockState");
        this.newBlockState = Preconditions.checkNotNull(newBlockState, "newBlockState");
        this.source = Preconditions.checkNotNull(source, "source");
        this.reason = Preconditions.checkNotNull(reason, "reason");
    }

    @Nonnull
    @Override
    public Block getCurrentBlock() {
        return currentBlock;
    }

    @Nonnull
    public BlockState getPreviousBlockState() {
        return previousBlockState;
    }

    @Nonnull
    public BlockState getNewBlockState() {
        return newBlockState;
    }

    public void setNewBlockState(@Nonnull BlockState newBlockState) {
        this.newBlockState = Preconditions.checkNotNull(newBlockState, "newBlockState");
    }

    @Nonnull
    public Entity getSource() {
        return source;
    }

    @Nonnull
    public ReplaceReason getReason() {
        return reason;
    }

    public enum ReplaceReason {
        /**
         * A player has placed a block.
         */
        PLAYER_PLACE,
        /**
         * A player has broken a block.
         */
        PLAYER_BREAK,
        /**
         * An entity has broken the block. For instance, it could be an endermen holding a grass block.
         */
        ENTITY_BREAK,
        /**
         * An entity has placed a block. For instance, an enderman is placing the block down.
         */
        ENTITY_PLACE,
        /**
         * An entity is replacing the block. For instance, a silverfish burrowing into stone.
         */
        ENTITY_REPLACE,
        /**
         * A liquid has displaced this block. For instance, redstone wire is washed away by water.
         */
        LIQUID,
        /**
         * A block has been moved or destroyed by a piston.
         */
        PISTON
    }
}
