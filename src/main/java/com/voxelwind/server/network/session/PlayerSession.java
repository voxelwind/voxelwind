package com.voxelwind.server.network.session;

import com.flowpowered.math.vector.Vector2i;
import com.flowpowered.math.vector.Vector3i;
import com.google.common.base.Preconditions;
import com.voxelwind.server.level.Level;
import com.voxelwind.server.level.chunk.Chunk;
import com.voxelwind.server.network.handler.NetworkPacketHandler;
import com.voxelwind.server.network.mcpe.packets.McpeLogin;
import com.voxelwind.server.network.mcpe.packets.McpeRequestChunkRadius;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerSession {
    private static final Logger LOGGER = LogManager.getLogger(PlayerSession.class);

    private final UserSession session;
    private final Set<Vector2i> sentChunks = Collections.newSetFromMap(new ConcurrentHashMap<>());
    private Vector3i position;
    private Level level;

    public PlayerSession(UserSession session) {
        this.session = session;
    }

    private class PlayerSessionNetworkPacketHandler implements NetworkPacketHandler {
        @Override
        public void handle(McpeLogin login) {
            throw new IllegalStateException("Login packet received but player session is currently active!");
        }

        @Override
        public void handle(McpeRequestChunkRadius packet) {
            Preconditions.checkState(level != null, "Player has not been spawned into a level.");
            Preconditions.checkState(position != null, "Player has no set position.");

            // Get current player's position in chunks.
            int chunkX = position.getX() >> 4;
            int chunkZ = position.getZ() >> 4;

            // Now get and send chunk data.
            Set<Vector2i> chunksForRadius = new HashSet<>();

            for (int x = -packet.getRadius(); x <= packet.getRadius(); x++) {
                for (int z = -packet.getRadius(); z <= packet.getRadius(); z++) {
                    int newChunkX = chunkX + x, newChunkZ = chunkZ + z;
                    Vector2i chunkCoords = new Vector2i(newChunkX, newChunkZ);
                    chunksForRadius.add(chunkCoords);

                    if (sentChunks.add(chunkCoords)) {
                        // Already sent, don't need to resend.
                        continue;
                    }

                    level.getChunk(newChunkX, newChunkZ).whenCompleteAsync((chunk, throwable) -> {
                        if (throwable != null) {
                            LOGGER.error("Unable to load chunk", throwable);
                            return;
                        }
                        session.queuePackageForSend(chunk.getChunkDataPacket());
                    });
                }
            }

            sentChunks.retainAll(chunksForRadius);
        }
    }
}
