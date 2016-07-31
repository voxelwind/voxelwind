package com.voxelwind.server.level;

import com.voxelwind.server.level.provider.ChunkProvider;

public class LevelCreator {
    private final String name;
    private final ChunkProvider chunkProvider;

    public LevelCreator(String name, ChunkProvider chunkProvider) {
        this.name = name;
        this.chunkProvider = chunkProvider;
    }

    public String getName() {
        return name;
    }

    public ChunkProvider getChunkProvider() {
        return chunkProvider;
    }
}
