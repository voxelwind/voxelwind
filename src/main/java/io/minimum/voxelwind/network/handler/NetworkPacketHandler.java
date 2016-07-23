package io.minimum.voxelwind.network.handler;

import io.minimum.voxelwind.network.mcpe.packets.McpeLogin;

public interface NetworkPacketHandler {
    void handle(McpeLogin login);
}
