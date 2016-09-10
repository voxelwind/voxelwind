package com.voxelwind.server.game.level.provider;

import com.voxelwind.api.game.level.ChunkSnapshot;
import com.voxelwind.api.game.level.Level;

public interface PersistableChunkProvider {
    void saveChunk(Level level, int x, int z, ChunkSnapshot snapshot);
}
