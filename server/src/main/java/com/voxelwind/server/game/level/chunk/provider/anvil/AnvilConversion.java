package com.voxelwind.server.game.level.chunk.provider.anvil;

import com.voxelwind.api.game.level.Chunk;
import com.voxelwind.api.game.level.Level;
import com.voxelwind.nbt.tags.*;
import com.voxelwind.server.game.level.chunk.ChunkSection;
import com.voxelwind.server.game.level.chunk.SectionedChunk;
import com.voxelwind.server.game.level.util.NibbleArray;
import gnu.trove.map.hash.TIntObjectHashMap;
import lombok.experimental.UtilityClass;

import java.util.Map;

@UtilityClass
public class AnvilConversion {
    public static Chunk convertChunkToVoxelwind(Map<String, Tag<?>> levelData, Level level) {
        TIntObjectHashMap<Map<String, Tag<?>>> sectionMap = generateSectionsMap(levelData);

        // Translate block data
        int cx = ((IntTag) levelData.get("xPos")).getValue();
        int cz = ((IntTag) levelData.get("zPos")).getValue();
        SectionedChunk chunk = new SectionedChunk(cx, cz, level);
        for (int ySec = 0; ySec < 16; ySec++) {
            Map<String, Tag<?>> map = sectionMap.get(ySec);
            if (map != null) {
                byte[] blockIds = ((ByteArrayTag) map.get("Blocks")).getValue();
                NibbleArray data = new NibbleArray(((ByteArrayTag) map.get("Data")).getValue());
                NibbleArray skyLight = new NibbleArray(((ByteArrayTag) map.get("SkyLight")).getValue());
                NibbleArray blockLight = new NibbleArray(((ByteArrayTag) map.get("BlockLight")).getValue());

                // Copy all the data we can directly.
                ChunkSection section = chunk.getOrCreateSection(ySec);
                section.getBlockLight().copyFrom(blockLight);
                section.getSkyLight().copyFrom(skyLight);

                // Block IDs and data require remapping.
                for (int x = 0; x < 16; x++) {
                    for (int z = 0; z < 16; z++) {
                        for (int y = 0; y < 16; y++) {
                            int pos = anvilBlockPosition(x, (ySec * 16) + y, z);
                            section.setBlockId(x, y, z, blockIds[pos]);
                            section.setBlockData(x, y, z, data.get(pos));
                        }
                    }
                }
            }
        }

        chunk.recalculateHeightMap();
        return chunk;
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

    private static int anvilBlockPosition(int x, int y, int z) {
        return y * 16 * 16 + z * 16 + x;
    }
}
