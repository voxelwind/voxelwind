package com.voxelwind.server.game.level.block.behaviors;

import com.flowpowered.math.vector.Vector3i;
import com.google.common.base.Preconditions;
import com.voxelwind.api.game.Metadata;
import com.voxelwind.api.game.item.ItemStack;
import com.voxelwind.api.game.level.Chunk;
import com.voxelwind.api.game.level.block.Block;
import com.voxelwind.api.game.level.block.BlockState;
import com.voxelwind.api.game.level.block.BlockType;
import com.voxelwind.api.game.level.block.BlockTypes;
import com.voxelwind.api.game.util.data.BlockFace;
import com.voxelwind.api.server.Player;
import com.voxelwind.api.server.event.block.BlockReplaceEvent;
import com.voxelwind.server.game.level.VoxelwindLevel;
import com.voxelwind.server.game.level.block.BasicBlockState;
import com.voxelwind.server.game.level.block.BlockBehaviors;
import lombok.experimental.UtilityClass;

import java.util.Optional;

@UtilityClass
public class BehaviorUtils {
    public static boolean setBlockState(Player player, Vector3i position, BlockState state) {
        int chunkX = position.getX() >> 4;
        int chunkZ = position.getZ() >> 4;

        Optional<Chunk> chunkOptional = player.getLevel().getChunkIfLoaded(chunkX, chunkZ);
        if (!chunkOptional.isPresent()) {
            // Chunk not loaded, danger ahead!
            return false;
        }

        Block old = chunkOptional.get().getBlock(position.getX() & 0x0f, position.getY(), position.getZ() & 0x0f);
        if (!canProceed(old, state, player)) {
            return false;
        }
        chunkOptional.get().setBlock(position.getX() & 0x0f, position.getY(), position.getZ() & 0x0f, state);
        ((VoxelwindLevel) player.getLevel()).broadcastBlockUpdate(position);
        return true;
    }

    public static boolean replaceBlockState(Player player, Block block, BlockState replacementState) {
        if (!canProceed(block, replacementState, player)) {
            return false;
        }
        block.getChunk().setBlock(block.getChunkLocation().getX(), block.getChunkLocation().getY(), block.getChunkLocation().getZ(), replacementState);
        ((VoxelwindLevel) player.getLevel()).broadcastBlockUpdate(block.getLevelLocation());
        return true;
    }

    private static boolean canProceed(Block block, BlockState newState, Player player) {
        BlockReplaceEvent event = new BlockReplaceEvent(block, block.getBlockState(), new BasicBlockState(BlockTypes.AIR, null, null),
                player, BlockReplaceEvent.ReplaceReason.PLAYER_PLACE);
        player.getServer().getEventManager().fire(event);
        return event.getResult() == BlockReplaceEvent.Result.CONTINUE;
    }

    public static BlockState createBlockState(Vector3i position, BlockFace face, ItemStack stack) {
        Preconditions.checkNotNull(stack, "stack");

        if (!(stack.getItemType() instanceof BlockType)) {
            throw new IllegalArgumentException("Item type " + stack.getItemType().getName() + " is not a block type.");
        }

        // Consult block behavior for the relevant block.
        Optional<BlockState> overrideOptional = BlockBehaviors.getBlockBehavior((BlockType) stack.getItemType())
                .overrideBlockPlacement(position, face, stack);
        if (overrideOptional.isPresent()) {
            return overrideOptional.get();
        }

        BlockType blockType = (BlockType) stack.getItemType();
        Optional<Metadata> itemData = stack.getItemData();
        Metadata blockData = itemData.isPresent() ? itemData.get() : null;
        return new BasicBlockState(blockType, blockData, null);
    }
}
