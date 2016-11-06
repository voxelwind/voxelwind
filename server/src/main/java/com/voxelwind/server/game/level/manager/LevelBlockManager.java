package com.voxelwind.server.game.level.manager;

import com.flowpowered.math.vector.Vector3i;
import com.voxelwind.api.game.level.block.Block;
import com.voxelwind.server.game.level.VoxelwindLevel;
import com.voxelwind.server.game.level.block.BlockBehavior;
import com.voxelwind.server.game.level.block.BlockBehaviors;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class LevelBlockManager {
    private static final int MAXIMUM_BLOCKS_TICKED_PER_TICK = 50;

    private final VoxelwindLevel level;
    private final Queue<Vector3i> blocksToTick = new ConcurrentLinkedQueue<>();

    public LevelBlockManager(VoxelwindLevel level) {
        this.level = level;
    }

    public void queueBlock(Block block) {
        blocksToTick.add(block.getLevelLocation());
    }

    public void dequeueBlock(Block block) {
        blocksToTick.remove(block.getLevelLocation());
    }

    public void onTick() {
        // Grab the first 50 (or all entries) in the queue
        List<Vector3i> willTick = new ArrayList<>();
        Vector3i location;
        while (willTick.size() < MAXIMUM_BLOCKS_TICKED_PER_TICK && ((location = blocksToTick.poll()) != null)) {
            willTick.add(location);
        }

        // Try to tick all blocks.
        for (Vector3i vector3i : willTick) {
            // Make sure the relevant chunk is loaded.
            Optional<Block> blockOptional = level.getBlockIfChunkLoaded(vector3i);
            if (!blockOptional.isPresent()) {
                // Can't tick because chunk isn't loaded, continue.
                blocksToTick.add(vector3i);
                continue;
            }

            Block block = blockOptional.get();
            BlockBehavior behavior = BlockBehaviors.getBlockBehavior(block.getBlockState().getBlockType());
            if (!behavior.handleBlockTick(level.getServer(), block)) {
                blocksToTick.add(vector3i);
            }
        }
    }
}
