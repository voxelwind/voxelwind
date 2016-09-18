package com.voxelwind.api.server.event.block;

import com.voxelwind.api.game.level.block.Block;
import com.voxelwind.api.server.event.Event;

import javax.annotation.Nonnull;

/**
 * This interface is implemented by block-related events.
 */
public interface BlockEvent extends Event {
    @Nonnull
    Block getCurrentBlock();
}
