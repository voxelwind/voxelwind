package com.voxelwind.server.game.serializer;

import com.voxelwind.api.game.Metadata;
import com.voxelwind.api.game.item.ItemStack;
import com.voxelwind.api.game.item.ItemType;
import com.voxelwind.api.game.item.data.Directional;
import com.voxelwind.api.game.level.block.BlockState;
import com.voxelwind.api.game.level.blockentities.BlockEntity;
import com.voxelwind.api.game.util.data.BlockFace;
import com.voxelwind.nbt.tags.CompoundTag;
import gnu.trove.map.TObjectShortMap;
import gnu.trove.map.TShortObjectMap;
import gnu.trove.map.hash.TObjectShortHashMap;
import gnu.trove.map.hash.TShortObjectHashMap;

public class GenericDirectionalSerializer implements Serializer {
    private static final TShortObjectMap<BlockFace> FACES = new TShortObjectHashMap<>();
    private static final TObjectShortMap<BlockFace> FACES_REVERSE = new TObjectShortHashMap<>();

    static {
        FACES.put((short) 2, BlockFace.NORTH);
        FACES.put((short) 3, BlockFace.SOUTH);
        FACES.put((short) 4, BlockFace.WEST);
        FACES.put((short) 5, BlockFace.EAST);

        FACES.forEachEntry((k, v) -> {
            FACES_REVERSE.put(v, k);
            return true;
        });
    }

    @Override
    public CompoundTag readNBT(BlockState block) {
        return null;
    }

    @Override
    public short readMetadata(BlockState block) {
        Directional directional = getBlockData(block);
        return directional == null ? 0 : FACES_REVERSE.get(directional.getFace());
    }

    @Override
    public CompoundTag readNBT(ItemStack itemStack) {
        return null;
    }

    @Override
    public short readMetadata(ItemStack itemStack) {
        return 0;
    }

    @Override
    public Metadata writeMetadata(ItemType block, short metadata) {
        BlockFace face = FACES.get(metadata);
        return face != null ? new Directional(face) : null;
    }

    @Override
    public BlockEntity writeNBT(ItemType block, CompoundTag nbtTag) {
        return null;
    }
}
