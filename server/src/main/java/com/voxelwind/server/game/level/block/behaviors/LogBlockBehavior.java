package com.voxelwind.server.game.level.block.behaviors;

import com.flowpowered.math.vector.Vector3i;
import com.voxelwind.api.game.Metadata;
import com.voxelwind.api.game.item.ItemStack;
import com.voxelwind.api.game.item.data.wood.Log;
import com.voxelwind.api.game.item.data.wood.Wood;
import com.voxelwind.api.game.level.block.BlockState;
import com.voxelwind.api.game.level.block.BlockType;
import com.voxelwind.api.game.util.data.BlockFace;
import com.voxelwind.api.game.util.data.LogDirection;
import com.voxelwind.server.game.level.block.BasicBlockState;

import java.util.Optional;

public class LogBlockBehavior extends SimpleBlockBehavior {
    @Override
    public Optional<BlockState> overrideBlockPlacement(Vector3i against, BlockFace face, ItemStack itemStack) {
        // Depending on face, in which direction should this log be facing?
        LogDirection direction;
        switch (face) {
            case BOTTOM:
            case TOP:
                direction = LogDirection.VERTICAL;
                break;
            case NORTH:
            case SOUTH:
                direction = LogDirection.HORIZONTAL_X;
                break;
            case EAST:
            case WEST:
                direction = LogDirection.HORIZONTAL_Z;
                break;
            default:
                return Optional.empty();
        }

        Optional<Metadata> woodOptional = itemStack.getItemData();
        if (woodOptional.isPresent()) {
            if (woodOptional.get() instanceof Wood) {
                return Optional.of(new BasicBlockState((BlockType) itemStack.getItemType(), Log.of(((Wood) woodOptional.get()).getSpecies(), direction), null));
            }
        }
        return Optional.empty();
    }
}
