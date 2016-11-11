package com.voxelwind.api.server;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import java.nio.file.Path;

@Builder
@Value
public class LevelCreator {
    @NonNull
    private final String name;
    @NonNull
    private final WorldStorage storage;
    private final boolean enableWrite;
    @NonNull
    private final Path worldPath;
    private final boolean loadSpawnChunks;

    public enum WorldStorage {
        ANVIL,
        NULL
    }
}
