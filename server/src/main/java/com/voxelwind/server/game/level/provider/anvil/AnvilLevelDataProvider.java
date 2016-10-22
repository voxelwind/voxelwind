package com.voxelwind.server.game.level.provider.anvil;

import com.flowpowered.math.vector.Vector3f;
import com.flowpowered.math.vector.Vector3i;
import com.voxelwind.nbt.io.NBTReader;
import com.voxelwind.nbt.io.NBTReaders;
import com.voxelwind.nbt.tags.CompoundTag;
import com.voxelwind.nbt.tags.IntTag;
import com.voxelwind.nbt.tags.LongTag;
import com.voxelwind.nbt.tags.Tag;
import com.voxelwind.server.game.level.provider.LevelDataProvider;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.zip.GZIPInputStream;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class AnvilLevelDataProvider implements LevelDataProvider {
    private final Vector3f spawnLocation;
    private final int savedTime;

    public static AnvilLevelDataProvider load(@NonNull Path levelDatPath) throws IOException {
        // level.dat is Notchian, so it's big-endian and GZIP compressed
        CompoundTag tag;
        try (NBTReader reader = NBTReaders.createBigEndianReader(new GZIPInputStream(Files.newInputStream(levelDatPath)))) {
            tag = (CompoundTag) reader.readTag();
        }

        CompoundTag dataTag = (CompoundTag) tag.getValue().get("Data");
        Map<String, Tag<?>> map = dataTag.getValue();

        Vector3i out = new Vector3i(((IntTag) map.get("SpawnX")).getValue(), ((IntTag) map.get("SpawnY")).getValue(), ((IntTag) map.get("SpawnZ")).getValue());
        long dayTime = ((LongTag) map.get("DayTime")).getValue();
        return new AnvilLevelDataProvider(out.toFloat(), (int) dayTime);
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
