package com.voxelwind.server.game.level.chunk.provider.anvil;

import com.flowpowered.math.vector.Vector3f;
import com.flowpowered.math.vector.Vector3i;
import com.voxelwind.nbt.io.NBTReader;
import com.voxelwind.nbt.io.NBTReaders;
import com.voxelwind.nbt.tags.CompoundTag;
import com.voxelwind.nbt.tags.IntTag;
import com.voxelwind.nbt.tags.LongTag;
import com.voxelwind.nbt.tags.Tag;
import com.voxelwind.server.game.level.chunk.provider.LevelDataProvider;
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
    private final long seed;

    public static AnvilLevelDataProvider load(@NonNull Path levelDatPath) throws IOException {
        // level.dat is Notchian, so it's big-endian and GZIP compressed
        CompoundTag tag;
        try (NBTReader reader = NBTReaders.createBigEndianReader(new GZIPInputStream(Files.newInputStream(levelDatPath)))) {
            tag = (CompoundTag) reader.readTag();
        }

        CompoundTag dataTag = (CompoundTag) tag.getValue().get("Data");
        Map<String, Tag<?>> map = dataTag.getValue();

        Vector3i out = new Vector3i(((IntTag) map.get("SpawnX")).getPrimitiveValue(), ((IntTag) map.get("SpawnY")).getPrimitiveValue(),
                ((IntTag) map.get("SpawnZ")).getPrimitiveValue());
        long dayTime = ((LongTag) map.get("DayTime")).getPrimitiveValue();
        long seed = ((LongTag) map.get("RandomSeed")).getPrimitiveValue();
        return new AnvilLevelDataProvider(out.toFloat(), (int) dayTime, seed);
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

    @Override
    public long getSeed() {
        return seed;
    }
}
