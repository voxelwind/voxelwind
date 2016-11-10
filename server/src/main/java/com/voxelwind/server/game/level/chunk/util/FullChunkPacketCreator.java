package com.voxelwind.server.game.level.chunk.util;

import com.voxelwind.server.network.mcpe.packets.McpeBatch;

public interface FullChunkPacketCreator {
    McpeBatch toFullChunkData();
}
