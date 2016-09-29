package com.voxelwind.api.game.level.blockentities;

import com.voxelwind.api.game.level.block.FlowerType;

/**
 * Represents a flowerpot holding a specific {@link com.voxelwind.api.game.level.block.FlowerType}
 */
public interface FlowerpotBlockEntity extends BlockEntity {
    FlowerType getFlowerType();
}
