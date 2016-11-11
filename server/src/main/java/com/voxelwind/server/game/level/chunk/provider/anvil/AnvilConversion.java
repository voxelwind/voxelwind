package com.voxelwind.server.game.level.chunk.provider.anvil;

import com.voxelwind.api.game.level.Chunk;
import com.voxelwind.api.game.level.Level;
import com.voxelwind.nbt.tags.*;
import com.voxelwind.server.game.level.chunk.SectionedChunk;
import com.voxelwind.server.game.level.util.NibbleArray;
import gnu.trove.map.hash.TIntObjectHashMap;
import lombok.experimental.UtilityClass;

import java.util.Map;

@UtilityClass
public class AnvilConversion {
    public static Chunk convertChunkToVoxelwind(Map<String, Tag<?>> levelData, Level level) {
        TIntObjectHashMap<Map<String, Tag<?>>> sectionMap = generateSectionsMap(levelData);

        // Translate section data
        ChunkSection[] sections = new ChunkSection[8];
        for (int ySec = 0; ySec < 8; ySec++) {
            Map<String, Tag<?>> map = sectionMap.get(ySec);
            if (map != null) {
                byte[] blockIds = ((ByteArrayTag) map.get("Blocks")).getValue();
                NibbleArray data = new NibbleArray(((ByteArrayTag) map.get("Data")).getValue());
                NibbleArray skyLight = new NibbleArray(((ByteArrayTag) map.get("SkyLight")).getValue());
                NibbleArray blockLight = new NibbleArray(((ByteArrayTag) map.get("BlockLight")).getValue());
                sections[ySec] = new ChunkSection(blockIds, data, skyLight, blockLight);
            }
        }

        int x = ((IntTag) levelData.get("xPos")).getValue();
        int z = ((IntTag) levelData.get("zPos")).getValue();
        return new SectionedChunk(sections, x, z, level);
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
