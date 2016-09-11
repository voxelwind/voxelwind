package com.voxelwind.server.game.level.block.behaviors;

import com.flowpowered.math.vector.Vector3i;
import com.google.common.base.Preconditions;
import com.voxelwind.api.game.item.ItemStack;
import com.voxelwind.api.game.item.data.ItemData;
import com.voxelwind.api.game.level.Chunk;
import com.voxelwind.api.game.level.Level;
import com.voxelwind.api.game.level.block.BlockData;
import com.voxelwind.api.game.level.block.BlockState;
import com.voxelwind.api.game.level.block.BlockType;
import com.voxelwind.api.server.Player;
import com.voxelwind.server.game.level.VoxelwindLevel;
import com.voxelwind.server.game.level.block.BasicBlockState;
import lombok.experimental.UtilityClass;

import java.util.Optional;

@UtilityClass
public class BehaviorUtils {
    public static boolean setBlockState(Player player, Level level, Vector3i position, BlockState state) {
        // TODO: Events
        int chunkX = position.getX() >> 4;
        int chunkZ = position.getZ() >> 4;

        Optional<Chunk> chunkOptional = level.getChunkIfLoaded(chunkX, chunkZ);
        if (!chunkOptional.isPresent()) {
            // Chunk not loaded, danger ahead!
            return false;
        }

        chunkOptional.get().setBlock(position.getX() & 0x0f, position.getY(), position.getZ() & 0x0f, state);
        ((VoxelwindLevel) level).broadcastBlockUpdate(position);
        return true;
    }

    public static BlockState createBlockState(ItemStack stack) {
        Preconditions.checkNotNull(stack, "stack");

        if (!(stack.getItemType() instanceof BlockType)) {
            throw new IllegalArgumentException("Item type " + stack.getItemType().getName() + " is not a block type.");
        }
        BlockType blockType = (BlockType) stack.getItemType();
        Optional<ItemData> itemData = stack.getItemData();
        BlockData blockData = null;
        if (itemData.isPresent()) {
            if (itemData.get() instanceof BlockData) {
                blockData = (BlockData) itemData.get();
            }
        }

        return new BasicBlockState(blockType, blockData);
    }
}
