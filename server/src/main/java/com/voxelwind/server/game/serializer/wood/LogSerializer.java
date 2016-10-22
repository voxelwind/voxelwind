package com.voxelwind.server.game.serializer.wood;

import com.google.common.collect.ImmutableMap;
import com.voxelwind.api.game.Metadata;
import com.voxelwind.api.game.item.ItemType;
import com.voxelwind.api.game.item.data.wood.Log;
import com.voxelwind.api.game.level.block.BlockState;
import com.voxelwind.api.game.level.block.BlockType;
import com.voxelwind.api.game.level.block.BlockTypes;
import com.voxelwind.api.game.util.data.LogDirection;
import com.voxelwind.api.game.util.data.TreeSpecies;

import java.util.Arrays;
import java.util.Map;

public class LogSerializer extends SimpleWoodSerializer {
    private static final Map<BlockType, TreeSpecies[]> BLOCK_SPECIES_MAPPING = ImmutableMap.<BlockType, TreeSpecies[]>builder()
            .put(BlockTypes.WOOD, new TreeSpecies[]{TreeSpecies.OAK, TreeSpecies.SPRUCE, TreeSpecies.BIRCH, TreeSpecies.JUNGLE})
            .put(BlockTypes.ACACIA_WOOD, new TreeSpecies[]{TreeSpecies.ACACIA, TreeSpecies.DARK_OAK})
            .build();
    private static final LogDirection[] DIRECTIONS = LogDirection.values();

    @Override
    public short readMetadata(BlockState block) {
        Log log = getBlockData(block);
        if (log == null) {
            return 0;
        }

        TreeSpecies[] availableSpecies = BLOCK_SPECIES_MAPPING.get(block.getBlockType());
        if (availableSpecies == null) {
            throw new IllegalArgumentException("Did not find mapping for " + block.getBlockType());
        }

        int speciesBlockData = Arrays.binarySearch(availableSpecies, log.getSpecies(), null);
        if (speciesBlockData < 0) {
            throw new IllegalArgumentException("Tree species " + log.getSpecies() + " isn't valid for " + block.getBlockType());
        }
        int directionalData = log.getDirection().ordinal();

        return (short) (directionalData * 4 + speciesBlockData);
    }

    @Override
    public Metadata writeMetadata(ItemType block, short metadata) {
        if (!(block instanceof BlockType)) {
            return null;
        }

        TreeSpecies[] availableSpecies = BLOCK_SPECIES_MAPPING.get(block);
        if (availableSpecies == null) {
            return null;
        }

        // Determine species and direction.
        int species = metadata & 0x03;
        int direction = metadata / 4;

        return Log.of(availableSpecies[species], DIRECTIONS[direction]);
    }
}
