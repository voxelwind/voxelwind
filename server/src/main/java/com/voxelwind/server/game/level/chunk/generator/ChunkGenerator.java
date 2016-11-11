package com.voxelwind.server.game.level.chunk.generator;

import com.voxelwind.api.game.level.Chunk;
import com.voxelwind.api.game.level.Level;

import java.util.Random;

public interface ChunkGenerator {
    void generate(Level level, Chunk chunk, Random random);
}
