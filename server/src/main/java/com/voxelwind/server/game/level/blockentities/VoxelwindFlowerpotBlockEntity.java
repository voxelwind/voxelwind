package com.voxelwind.server.game.level.blockentities;

import com.voxelwind.api.game.level.block.FlowerType;
import com.voxelwind.api.game.level.blockentities.FlowerpotBlockEntity;

/**
 * @author geNAZt
 * @version 1.0
 */
public class VoxelwindFlowerpotBlockEntity implements FlowerpotBlockEntity {
    private final FlowerType flowerType;

    public VoxelwindFlowerpotBlockEntity(FlowerType flowerType) {
        this.flowerType = flowerType;
    }

    @Override
    public FlowerType getFlowerType() {
        return this.flowerType;
    }
}
