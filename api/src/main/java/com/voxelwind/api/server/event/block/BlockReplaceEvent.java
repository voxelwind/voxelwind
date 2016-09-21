package com.voxelwind.api.server.event.block;

import com.google.common.base.Preconditions;
import com.voxelwind.api.game.entities.Entity;
import com.voxelwind.api.game.level.block.Block;
import com.voxelwind.api.game.level.block.BlockState;
import com.voxelwind.api.server.event.block.iface.BlockReplacer;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

/**
 * This event will be fired upon a replacement of a block.
 */
@ParametersAreNonnullByDefault
public class BlockReplaceEvent implements BlockEvent {
    private final Block currentBlock;
    private final BlockState previousBlockState;
    private final BlockState newBlockState;
    private final BlockReplacer source;
    private final ReplaceReason reason;
    private Result result = Result.CONTINUE;

    public BlockReplaceEvent(Block currentBlock, BlockState previousBlockState, BlockState newBlockState, BlockReplacer source, ReplaceReason reason) {
        this.currentBlock = Preconditions.checkNotNull(currentBlock, "currentBlock");
        this.previousBlockState = Preconditions.checkNotNull(previousBlockState, "previousBlockState");
        this.newBlockState = Preconditions.checkNotNull(newBlockState, "newBlockState");
        this.source = Preconditions.checkNotNull(source, "source");
        this.reason = Preconditions.checkNotNull(reason, "reason");
    }

    /**
     * Returns the current block in the level. It reflects the old state (the new one is not yet applied).
     * @return the current block
     */
    @Nonnull
    @Override
    public Block getCurrentBlock() {
        return currentBlock;
    }

    /**
     * Returns the previous block state.
     * @return the previous block state
     */
    @Nonnull
    public BlockState getPreviousBlockState() {
        return previousBlockState;
    }

    /**
     * Returns the new block state.
     * @return the new block state
     */
    @Nonnull
    public BlockState getNewBlockState() {
        return newBlockState;
    }

    /**
     * Returns the actor that initiated this action.
     * @return the actor
     */
    @Nonnull
    public BlockReplacer getSource() {
        return source;
    }

    /**
     * Returns the reason why this block is being replaced.
     * @return
     */
    @Nonnull
    public ReplaceReason getReason() {
        return reason;
    }

    /**
     * Returns the current result of this event.
     * @return the current result
     */
    @Nonnull
    public Result getResult() {
        return result;
    }

    /**
     * Sets a new result for this event.
     * @param result the new result
     */
    public void setResult(Result result) {
        this.result = Preconditions.checkNotNull(result, "result");
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

    private enum Result {
        /**
         * The operation should be continued and the block placed into the world.
         */
        CONTINUE,
        /**
         * The operation should be aborted and the block reverted to normal.
         */
        REVERT
    }
}
