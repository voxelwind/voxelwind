package com.voxelwind.server.network.session;

import com.flowpowered.math.vector.Vector2i;
import com.flowpowered.math.vector.Vector3i;
import com.spotify.futures.CompletableFutures;
import com.voxelwind.server.level.Level;
import com.voxelwind.server.level.chunk.Chunk;
import com.voxelwind.server.level.entities.LivingEntity;
import com.voxelwind.server.network.handler.NetworkPacketHandler;
import com.voxelwind.server.network.mcpe.packets.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class PlayerSession extends LivingEntity {
    private static final Logger LOGGER = LogManager.getLogger(PlayerSession.class);

    private final UserSession session;
    private final Set<Vector2i> sentChunks = Collections.newSetFromMap(new ConcurrentHashMap<>());
    private boolean sprinting = false;
    private boolean sneaking = false;
    private boolean firstChunksSent = false;

    public PlayerSession(UserSession session, Level level) {
        super(level, level.getChunkProvider().getSpawn()); // not yet actually spawned
        this.session = session;
    }

    public void doInitialSpawn() {
        McpeStartGame startGame = new McpeStartGame();
        startGame.setSeed(-1);
        startGame.setDimension((byte) 0);
        startGame.setGenerator(1);
        startGame.setGamemode(0);
        startGame.setEntityId(getEntityId());
        startGame.setSpawnLocation(getPosition().toInt());
        startGame.setPosition(getPosition());
        session.addToSendQueue(startGame);

        McpeAdventureSettings settings = new McpeAdventureSettings();
        settings.setPlayerPermissions(3);
        session.addToSendQueue(settings);

        McpeSetSpawnPosition spawnPosition = new McpeSetSpawnPosition();
        spawnPosition.setPosition(getLevel().getChunkProvider().getSpawn().toInt());
        session.addToSendQueue(spawnPosition);
    }

    public UserSession getUserSession() {
        return session;
    }

    NetworkPacketHandler getPacketHandler() {
        return new PlayerSessionNetworkPacketHandler();
    }

    private CompletableFuture<List<Chunk>> sendRadius(int radius, boolean updateSent) {
        // Get current player's position in chunks.
        Vector3i positionAsInt = getPosition().toInt();
        int chunkX = positionAsInt.getX() >> 4;
        int chunkZ = positionAsInt.getZ() >> 4;

        // Now get and send chunk data.
        Set<Vector2i> chunksForRadius = new HashSet<>();
        List<CompletableFuture<Chunk>> completableFutures = new ArrayList<>();

        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                int newChunkX = chunkX + x, newChunkZ = chunkZ + z;
                Vector2i chunkCoords = new Vector2i(newChunkX, newChunkZ);
                chunksForRadius.add(chunkCoords);

                if (updateSent) {
                    if (!sentChunks.add(chunkCoords)) {
                        // Already sent, don't need to resend.
                        continue;
                    }
                }

                completableFutures.add(getLevel().getChunkProvider().get(newChunkX, newChunkZ));
            }
        }

        if (updateSent) {
            sentChunks.retainAll(chunksForRadius);
        }

        return CompletableFutures.allAsList(completableFutures);
    }

    public void disconnect(String reason) {
        McpeDisconnect packet = new McpeDisconnect();
        packet.setMessage(reason);
        session.sendUrgentPackage(packet);

        // Wait a little bit and close their session
        session.getChannel().eventLoop().schedule(() -> {
            if (!session.isClosed()) {
                session.close();
            }
        }, 500, TimeUnit.MILLISECONDS);
    }

    private class PlayerSessionNetworkPacketHandler implements NetworkPacketHandler {
        @Override
        public void handle(McpeLogin packet) {
            throw new IllegalStateException("Login packet received but player session is currently active!");
        }

        @Override
        public void handle(McpeClientMagic packet) {
            throw new IllegalStateException("Client packet received but player session is currently active!");
        }

        @Override
        public void handle(McpeRequestChunkRadius packet) {
            int radius = Math.max(5, Math.min(16, packet.getRadius()));
            McpeChunkRadiusUpdated updated = new McpeChunkRadiusUpdated();
            updated.setRadius(radius);
            session.addToSendQueue(updated);

            sendRadius(radius, true).whenComplete((chunks, throwable) -> {
                if (throwable != null) {
                    LOGGER.error("Unable to load chunks for " + getUserSession().getAuthenticationProfile().getDisplayName(), throwable);
                    disconnect("Internal server error");
                    return;
                }

                chunks.stream().map(Chunk::getChunkDataPacket).forEach(session::addToSendQueue);

                if (!firstChunksSent) {
                    firstChunksSent = true;

                    // Ensure these will be in a future batch
                    session.getChannel().eventLoop().schedule(() -> {
                        McpePlayStatus status = new McpePlayStatus();
                        status.setStatus(McpePlayStatus.Status.PLAYER_SPAWN);
                        session.addToSendQueue(status);

                        McpeSetTime setTime = new McpeSetTime();
                        setTime.setTime(0);
                        setTime.setRunning(true);
                        session.addToSendQueue(setTime);

                        //McpeRespawn respawn = new McpeRespawn();
                        //respawn.setPosition(getLevel().getChunkProvider().getSpawn());
                        //session.addToSendQueue(respawn);
                    }, 500, TimeUnit.MILLISECONDS);
                }
            });
        }

        @Override
        public void handle(McpePlayerAction packet) {
            switch (packet.getAction()) {
                case ACTION_START_BREAK:
                    // Fire interact
                    break;
                case ACTION_ABORT_BREAK:
                    // No-op
                    break;
                case ACTION_STOP_BREAK:
                    // No-op
                    break;
                case ACTION_RELEASE_ITEM:
                    // Drop item, shoot bow, or dump bucket?
                    break;
                case ACTION_STOP_SLEEPING:
                    // Stop sleeping
                    break;
                case ACTION_SPAWN_SAME_DIMENSION:
                    // Clean up attributes?
                    break;
                case ACTION_JUMP:
                    // No-op
                    break;
                case ACTION_START_SPRINT:
                    sprinting = true;
                    // TODO: Update entity attributes
                    break;
                case ACTION_STOP_SPRINT:
                    sprinting = false;
                    // TODO: Update entity attributes
                    break;
                case ACTION_START_SNEAK:
                    sneaking = true;
                    // TODO: Update entity attributes
                    break;
                case ACTION_STOP_SNEAK:
                    sneaking = false;
                    // TODO: Update entity attributes
                    break;
                case ACTION_SPAWN_OVERWORLD:
                    // Clean up attributes?
                    break;
                case ACTION_SPAWN_NETHER:
                    // Clean up attributes?
                    break;
            }
        }

        @Override
        public void handle(McpeAnimate packet) {
            getLevel().getPacketManager().queuePacketForPlayers(packet);
        }
    }
}
