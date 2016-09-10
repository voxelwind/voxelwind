package com.voxelwind.server.game.item.behaviors;

import com.flowpowered.math.vector.Vector3i;
import com.voxelwind.api.game.level.Chunk;
import com.voxelwind.api.game.level.Level;
import com.voxelwind.api.game.level.block.BlockState;
import lombok.experimental.UtilityClass;

import java.util.Optional;

@UtilityClass
public class BehaviorUtils {
    public static boolean setBlockState(Level level, Vector3i position, BlockState state) {
        // TODO: Events
        int chunkX = position.getX() >> 4;
        int chunkZ = position.getZ() >> 4;

        Optional<Chunk> chunkOptional = level.getChunkIfLoaded(chunkX, chunkZ);
        if (!chunkOptional.isPresent()) {
            // Chunk not loaded, danger ahead!
            return false;
        }

        chunkOptional.get().setBlock(position.getX() & 0x0f, position.getY(), position.getZ() & 0x0f, state);
        return true;
    }
}
