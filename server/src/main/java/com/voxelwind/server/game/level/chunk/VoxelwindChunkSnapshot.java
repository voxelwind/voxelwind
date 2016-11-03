package com.voxelwind.server.game.level.chunk;

import com.flowpowered.math.vector.Vector3i;
import com.google.common.base.Preconditions;
import com.voxelwind.api.game.Metadata;
import com.voxelwind.api.game.level.ChunkSnapshot;
import com.voxelwind.api.game.level.block.BlockSnapshot;
import com.voxelwind.api.game.level.block.BlockType;
import com.voxelwind.api.game.level.block.BlockTypes;
import com.voxelwind.api.game.level.blockentities.BlockEntity;
import com.voxelwind.server.game.level.block.BasicBlockState;
import com.voxelwind.server.game.level.block.VoxelwindBlock;
import com.voxelwind.server.game.level.util.NibbleArray;
import com.voxelwind.server.game.serializer.MetadataSerializer;
import gnu.trove.map.TIntObjectMap;

import java.util.Optional;

class VoxelwindChunkSnapshot implements ChunkSnapshot {
    private final byte[] blockData;
    private final NibbleArray blockMetadata;
    private final NibbleArray skyLightData;
    private final NibbleArray blockLightData;
    private final TIntObjectMap<BlockEntity> blockEntities;

    private final int x;
    private final int z;

    VoxelwindChunkSnapshot(byte[] blockData, NibbleArray blockMetadata, NibbleArray skyLightData, NibbleArray blockLightData, TIntObjectMap<BlockEntity> blockEntities, int x, int z) {
        this.blockData = blockData;
        this.blockMetadata = blockMetadata;
        this.skyLightData = skyLightData;
        this.blockLightData = blockLightData;
        this.blockEntities = blockEntities;
        this.x = x;
        this.z = z;
    }

    @Override
    public int getX() {
        return 0;
    }

    @Override
    public int getZ() {
        return 0;
    }

    @Override
    public BlockSnapshot getBlock(int x, int y, int z) {
        checkPosition(x, y, z);
        int index = VoxelwindChunk.xyzIdx(x, y, z);
        byte data = blockData[index];

        Vector3i full = new Vector3i(x + (this.x * 16), y, z + (this.z * 16));

        BlockType type = BlockTypes.forId(data);
        Optional<Metadata> createdData;
        if (type.getMetadataClass() != null) {
            createdData = Optional.of(MetadataSerializer.deserializeMetadata(type, blockMetadata.get(index)));
        } else {
            createdData = Optional.empty();
        }

        // TODO: Add level and chunk
        return new VoxelwindBlock(null, null, full, new BasicBlockState(BlockTypes.forId(data), createdData.orElse(null), blockEntities.get(index)));
    }

    private static void checkPosition(int x, int y, int z) {
        Preconditions.checkArgument(x >= 0 && x <= 15, "x value (%s) not in range (0 to 15)", x);
        Preconditions.checkArgument(z >= 0 && z <= 15, "z value (%s) not in range (0 to 15)", z);
        Preconditions.checkArgument(y >= 0 && y < 128, "y value (%s) not in range (0 to 127)", y);
    }
}
