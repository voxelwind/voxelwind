package com.voxelwind.server.game.level.chunk.util;

import com.voxelwind.server.network.mcpe.packets.McpeWrapper;

public interface FullChunkPacketCreator {
    McpeWrapper toFullChunkData();
}
