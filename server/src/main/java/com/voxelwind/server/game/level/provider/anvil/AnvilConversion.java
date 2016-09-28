package com.voxelwind.server.game.level.provider.anvil;

import com.flowpowered.nbt.*;
import com.voxelwind.api.game.level.Level;
import com.voxelwind.server.game.level.chunk.VoxelwindChunk;
import com.voxelwind.server.game.level.util.NibbleArray;
import gnu.trove.map.hash.TIntObjectHashMap;
import lombok.experimental.UtilityClass;

@UtilityClass
public class AnvilConversion {
    public static VoxelwindChunk convertChunkToVoxelwind(CompoundMap levelData, Level level) {
        VoxelwindChunk destinationChunk = new VoxelwindChunk(level, ((IntTag) levelData.get("xPos")).getValue(), ((IntTag) levelData.get("zPos")).getValue());
        TIntObjectHashMap<CompoundMap> sectionMap = generateMap(levelData);

        // Translate block data
        for (int ySec = 0; ySec < 8; ySec++) {
            CompoundMap map = sectionMap.get(ySec);
            if (map == null) {
                for (int x = 0; x < 16; x++) {
                    for (int z = 0; z < 16; z++) {
                        for (int y = 0; y < 16; y++) {
                            destinationChunk.setBlockId(x, (ySec * 16) + y, z, 0, (short) 0, false);
                        }
                    }
                }
            } else {
                byte[] blockIds = ((ByteArrayTag) map.get("Blocks")).getValue();
                NibbleArray data = new NibbleArray(((ByteArrayTag) map.get("Data")).getValue());
                for (int x = 0; x < 16; x++) {
                    for (int z = 0; z < 16; z++) {
                        for (int y = 0; y < 16; y++) {
                            int pos = anvilBlockPosition(x, (ySec * 16) + y, z);
                            destinationChunk.setBlockId(x, (ySec * 16) + y, z, blockIds[pos], data.get(pos), false);
                        }
                    }
                }
            }
        }

        destinationChunk.recalculateLight();
        return destinationChunk;
    }

    private static TIntObjectHashMap<CompoundMap> generateMap(CompoundMap levelData) {
        ListTag<CompoundTag> sectionsList = (ListTag<CompoundTag>) levelData.get("Sections");
        TIntObjectHashMap<CompoundMap> map = new TIntObjectHashMap<CompoundMap>();
        for (CompoundTag tag : sectionsList.getValue()) {
            int y = ((ByteTag) tag.getValue().get("Y")).getValue();
            map.put(y, tag.getValue());
        }
        return map;
    }

    private static int anvilBlockPosition(int x, int y, int z) {
        return y * 16 * 16 + z * 16 + x;
    }
}
