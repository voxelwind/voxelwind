package com.voxelwind.server.game.level.provider.anvil;

import com.flowpowered.math.vector.Vector3f;
import com.flowpowered.math.vector.Vector3i;
import com.flowpowered.nbt.*;
import com.flowpowered.nbt.stream.NBTInputStream;
import com.voxelwind.server.game.level.provider.LevelDataProvider;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class AnvilLevelDataProvider implements LevelDataProvider {
    private final Vector3f spawnLocation;
    private final int savedTime;

    public static AnvilLevelDataProvider load(@NonNull Path levelDatPath) throws IOException {
        // level.dat is Notchian, so it's big-endian and GZIP compressed
        List<Tag<?>> levelDatTags = new ArrayList<>();
        CompoundTag tag;
        try (NBTInputStream stream = new NBTInputStream(new BufferedInputStream(Files.newInputStream(levelDatPath)), true)) {
            tag = (CompoundTag) stream.readTag();
        }

        CompoundTag dataTag = (CompoundTag) tag.getValue().get("Data");
        CompoundMap map = dataTag.getValue();

        Vector3i out = new Vector3i(((IntTag) map.get("SpawnX")).getValue(), ((IntTag) map.get("SpawnY")).getValue(), ((IntTag) map.get("SpawnZ")).getValue());
        int dayTime = ((IntTag) map.get("DayTime")).getValue();
        return new AnvilLevelDataProvider(out.toFloat(), dayTime);
    }

    @Override
    public Vector3f getSpawnLocation() {
        return spawnLocation;
    }

    @Override
    public void setSpawnLocation(Vector3f spawn) {

    }

    @Override
    public int getSavedTime() {
        return savedTime;
    }

    @Override
    public void setSavedTime(int time) {

    }
}
