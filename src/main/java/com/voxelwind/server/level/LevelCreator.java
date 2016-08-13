package com.voxelwind.server.level;

import com.voxelwind.server.level.provider.ChunkProvider;
import com.voxelwind.server.level.provider.LevelDataProvider;

public class LevelCreator {
    private final String name;
    private final ChunkProvider chunkProvider;
    private final LevelDataProvider dataProvider;

    public LevelCreator(String name, ChunkProvider chunkProvider, LevelDataProvider dataProvider) {
        this.name = name;
        this.chunkProvider = chunkProvider;
        this.dataProvider = dataProvider;
    }

    public String getName() {
        return name;
    }

    public ChunkProvider getChunkProvider() {
        return chunkProvider;
    }

    public LevelDataProvider getDataProvider() {
        return dataProvider;
    }
}
