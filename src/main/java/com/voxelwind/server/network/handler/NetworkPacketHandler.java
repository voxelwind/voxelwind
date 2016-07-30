package com.voxelwind.server.network.handler;

import com.voxelwind.server.network.mcpe.packets.McpeLogin;
import com.voxelwind.server.network.mcpe.packets.McpePlayerAction;
import com.voxelwind.server.network.mcpe.packets.McpeRequestChunkRadius;

public interface NetworkPacketHandler {
    void handle(McpeLogin packet);
    void handle(McpeRequestChunkRadius packet);
    void handle(McpePlayerAction packet);
}
