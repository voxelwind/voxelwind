package com.voxelwind.server.game.level.chunk.provider.anvil;

import com.voxelwind.api.game.level.Level;
import com.voxelwind.nbt.tags.*;
import com.voxelwind.server.game.level.chunk.VoxelwindChunk;
import com.voxelwind.server.game.level.util.NibbleArray;
import gnu.trove.map.hash.TIntObjectHashMap;
import lombok.experimental.UtilityClass;

import java.util.Map;

@UtilityClass
public class AnvilConversion {
    public static VoxelwindChunk convertChunkToVoxelwind(Map<String, Tag<?>> levelData, Level level) {
        VoxelwindChunk destinationChunk = new VoxelwindChunk(level, ((IntTag) levelData.get("xPos")).getValue(), ((IntTag) levelData.get("zPos")).getValue());
        TIntObjectHashMap<Map<String, Tag<?>>> sectionMap = generateSectionsMap(levelData);

        // Translate block data
        for (int ySec = 0; ySec < 8; ySec++) {
            Map<String, Tag<?>> map = sectionMap.get(ySec);
            if (map != null) {
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

    private static TIntObjectHashMap<Map<String, Tag<?>>> generateSectionsMap(Map<String, Tag<?>> levelData) {
        ListTag<CompoundTag> sectionsList = (ListTag<CompoundTag>) levelData.get("Sections");
        TIntObjectHashMap<Map<String, Tag<?>>> map = new TIntObjectHashMap<>();
        for (CompoundTag tag : sectionsList.getValue()) {
            int y = ((ByteTag) tag.getValue().get("Y")).getValue();
            map.put(y, tag.getValue());
        }
        return map;
    }
}
