package com.voxelwind.server.network.session;

import com.flowpowered.math.vector.Vector2i;
import com.flowpowered.math.vector.Vector3f;
import com.flowpowered.math.vector.Vector3i;
import com.google.common.base.Preconditions;
import com.spotify.futures.CompletableFutures;
import com.voxelwind.api.game.entities.misc.DroppedItem;
import com.voxelwind.api.game.inventories.Inventory;
import com.voxelwind.api.game.inventories.OpenableInventory;
import com.voxelwind.api.game.inventories.PlayerInventory;
import com.voxelwind.api.game.item.ItemStack;
import com.voxelwind.api.game.level.Chunk;
import com.voxelwind.api.game.level.block.Block;
import com.voxelwind.api.game.level.block.BlockType;
import com.voxelwind.api.game.level.block.BlockTypes;
import com.voxelwind.api.game.util.TextFormat;
import com.voxelwind.api.server.Player;
import com.voxelwind.api.server.Skin;
import com.voxelwind.api.server.command.CommandException;
import com.voxelwind.api.server.command.CommandNotFoundException;
import com.voxelwind.api.server.event.block.BlockReplaceEvent;
import com.voxelwind.api.server.event.player.PlayerJoinEvent;
import com.voxelwind.api.server.event.player.PlayerSpawnEvent;
import com.voxelwind.api.server.player.GameMode;
import com.voxelwind.api.server.player.PlayerMessageDisplayType;
import com.voxelwind.api.server.player.PopupMessage;
import com.voxelwind.api.server.player.TranslatedMessage;
import com.voxelwind.api.util.BlockFace;
import com.voxelwind.server.VoxelwindServer;
import com.voxelwind.server.game.entities.misc.VoxelwindDroppedItem;
import com.voxelwind.server.game.inventories.*;
import com.voxelwind.server.game.level.block.BlockBehavior;
import com.voxelwind.server.game.level.block.BlockBehaviors;
import com.voxelwind.server.game.level.block.behaviors.BehaviorUtils;
import com.voxelwind.server.game.level.VoxelwindLevel;
import com.voxelwind.server.game.level.block.BasicBlockState;
import com.voxelwind.server.game.level.chunk.VoxelwindChunk;
import com.voxelwind.server.game.entities.*;
import com.voxelwind.server.game.level.util.Attribute;
import com.voxelwind.server.game.level.util.BoundingBox;
import com.voxelwind.server.network.NetworkPackage;
import com.voxelwind.server.network.raknet.handler.NetworkPacketHandler;
import com.voxelwind.server.network.mcpe.packets.*;
import com.voxelwind.api.util.Rotation;
import gnu.trove.set.TLongSet;
import gnu.trove.set.hash.TLongHashSet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.net.InetSocketAddress;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class PlayerSession extends LivingEntity implements Player, InventoryObserver {
    private static final int REQUIRED_TO_SPAWN = 56;
    private static final Logger LOGGER = LogManager.getLogger(PlayerSession.class);

    private final McpeSession session;
    private final Set<Vector2i> sentChunks = Collections.newSetFromMap(new ConcurrentHashMap<>());
    private final TLongSet isViewing = new TLongHashSet();
    private GameMode gameMode = GameMode.SURVIVAL;
    private boolean spawned = false;
    private int viewDistance = 5;
    private final AtomicInteger windowIdGenerator = new AtomicInteger();
    private Inventory openedInventory;
    private byte openInventoryId = -1;
    private boolean hasMoved = false;
    private final VoxelwindBasePlayerInventory playerInventory = new VoxelwindBasePlayerInventory(this);
    private final VoxelwindServer vwServer;
    private final Set<UUID> playersSentForList = new HashSet<>();

    public PlayerSession(McpeSession session, VoxelwindLevel level) {
        super(EntityTypeData.PLAYER, level, level.getSpawnLocation(), session.getServer(), 20f);
        this.session = session;
        this.vwServer = session.getServer();
    }

    @Override
    public boolean onTick() {
        if (!spawned) {
            // Don't tick until the player has truly been spawned into the world.
            return true;
        }

        if (!super.onTick()) {
            return false;
        }

        // If the upstream session is closed, the player session should no longer be alive.
        if (session.isClosed()) {
            return false;
        }

        if (hasMoved) {
            hasMoved = false;
            if (isTeleported()) {
                sendMovePlayerPacket();
            }
            updateViewableEntities();
            sendNewChunks();
        }

        // Check for items on the ground.
        pickupAdjacent();

        // Update player list.
        // TODO: Packet doesn't currently encode correctly.
        //updatePlayerList();

        return true;
    }

    @Override
    public NetworkPackage createAddEntityPacket() {
        McpeAddPlayer addPlayer = new McpeAddPlayer();
        addPlayer.setEntityId(getEntityId());
        addPlayer.setVelocity(getMotion());
        addPlayer.setPosition(getPosition());
        addPlayer.setHeld(playerInventory.getStackInHand().orElse(null));
        addPlayer.setUsername(getMcpeSession().getAuthenticationProfile().getDisplayName());
        addPlayer.setUuid(getMcpeSession().getAuthenticationProfile().getIdentity());
        addPlayer.getMetadata().putAll(getMetadata());
        return addPlayer;
    }

    private void pickupAdjacent() {
        BoundingBox box = getBoundingBox().grow(1f, 0.5f, 1f);
        for (BaseEntity entity : getLevel().getEntityManager().getEntitiesInBounds(box)) {
            if (entity instanceof DroppedItem) {
                // TODO: Check pickup delay and fire events.
                if (getInventory().addItem(((VoxelwindDroppedItem) entity).getItemStack())) {
                    McpeTakeItem packetBroadcast = new McpeTakeItem();
                    packetBroadcast.setItemEntityId(entity.getEntityId());
                    packetBroadcast.setPlayerEntityId(getEntityId());
                    getLevel().getPacketManager().queuePacketForViewers(this, packetBroadcast);

                    McpeTakeItem packetSelf = new McpeTakeItem();
                    packetSelf.setItemEntityId(entity.getEntityId());
                    packetSelf.setPlayerEntityId(0);
                    session.addToSendQueue(packetSelf);

                    entity.remove();
                }
            }
        }
    }

    @Override
    protected void setPosition(Vector3f position) {
        super.setPosition(position);
        hasMoved = true;
    }

    @Override
    public void setRotation(@Nonnull Rotation rotation) {
        super.setRotation(rotation);
        hasMoved = true;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("Do not use remove() on player sessions. Use disconnect() instead.");
    }

    void removeInternal() {
        super.remove();
    }

    @Override
    public void setHealth(float health) {
        super.setHealth(health);
        sendAttributes();
    }

    @Override
    public void setMaximumHealth(float maximumHealth) {
        super.setMaximumHealth(maximumHealth);
        sendAttributes();
    }

    @Override
    protected void doDeath() {
        McpeEntityEvent event = new McpeEntityEvent();
        event.setEntityId(getEntityId());
        event.setEvent((byte) 3);
        getLevel().getPacketManager().queuePacketForViewers(this, event);

        Vector3f respawnLocation = getLevel().getSpawnLocation();

        McpeRespawn respawn = new McpeRespawn();
        respawn.setPosition(respawnLocation);
        session.addToSendQueue(respawn);

        setPosition(respawnLocation);
    }

    private void sendAttributes() {
        // Supported by MiNET:
        // - generic.health
        // - player.hunger
        // - player.level
        // - player.experience
        // - generic.movementSpeed
        // - generic.absorption
        Attribute health = new Attribute("generic.health", 0f, getMaximumHealth(), getHealth());
        Attribute hunger = new Attribute("player.hunger", 0f, 20f, 20f); // TODO: Implement hunger
        Attribute speed = new Attribute("generic.movementSpeed", 0, Float.MAX_VALUE, 0.1f);
        // TODO: Implement levels, movement speed, and absorption.

        McpeUpdateAttributes packet = new McpeUpdateAttributes();
        packet.getAttributes().add(health);
        packet.getAttributes().add(hunger);
        packet.getAttributes().add(speed);
        session.addToSendQueue(packet);
    }

    private void sendMovePlayerPacket() {
        McpeMovePlayer movePlayerPacket = new McpeMovePlayer();
        movePlayerPacket.setEntityId(getEntityId());
        movePlayerPacket.setPosition(getGamePosition());
        movePlayerPacket.setRotation(getRotation());
        movePlayerPacket.setMode(isTeleported());
        movePlayerPacket.setOnGround(isOnGround());
        session.addToSendQueue(movePlayerPacket);
    }

    public void doInitialSpawn() {
        // Fire PlayerSpawnEvent.
        // TODO: Fill this in of known player data.
        PlayerSpawnEvent event = new PlayerSpawnEvent(this, getLevel().getSpawnLocation(), getLevel(), Rotation.ZERO);
        session.getServer().getEventManager().fire(event);

        if (getLevel() != event.getSpawnLevel()) {
            getLevel().getEntityManager().unregister(this);
            ((VoxelwindLevel) event.getSpawnLevel()).getEntityManager().register(this);
            setEntityId(((VoxelwindLevel) event.getSpawnLevel()).getEntityManager().allocateEntityId());
        }
        setPosition(event.getSpawnLocation());
        setRotation(event.getRotation());
        hasMoved = false; // don't send duplicated packets

        // Send packets to spawn the player.
        McpeStartGame startGame = new McpeStartGame();
        startGame.setSeed(-1);
        startGame.setDimension((byte) 0);
        startGame.setGenerator(1);
        startGame.setGamemode(gameMode.ordinal());
        startGame.setEntityId(getEntityId());
        startGame.setSpawnLocation(getPosition().toInt());
        startGame.setPosition(getGamePosition());
        session.addToSendQueue(startGame);

        McpeAdventureSettings settings = new McpeAdventureSettings();
        settings.setPlayerPermissions(3);
        session.addToSendQueue(settings);

        McpeSetSpawnPosition spawnPosition = new McpeSetSpawnPosition();
        spawnPosition.setPosition(getLevel().getSpawnLocation().toInt());
        session.addToSendQueue(spawnPosition);

        sendMovePlayerPacket();
    }

    public McpeSession getMcpeSession() {
        return session;
    }

    NetworkPacketHandler getPacketHandler() {
        return new PlayerSessionNetworkPacketHandler();
    }

    private CompletableFuture<List<Chunk>> getChunksForRadius(int radius, boolean updateSent) {
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

                completableFutures.add(getLevel().getChunk(newChunkX, newChunkZ));
            }
        }

        if (updateSent) {
            sentChunks.retainAll(chunksForRadius);
        }

        return CompletableFutures.allAsList(completableFutures);
    }

    @Override
    public void disconnect(@Nonnull String reason) {
        session.disconnect(reason);
    }

    @Override
    public void sendMessage(@Nonnull String message) {
        Preconditions.checkNotNull(message, "message");
        McpeText text = new McpeText();
        text.setType(McpeText.TextType.RAW);
        text.setMessage(message);
        session.addToSendQueue(text);
    }

    public void updateViewableEntities() {
        synchronized (isViewing) {
            Collection<BaseEntity> inView = getLevel().getEntityManager().getEntitiesInDistance(getPosition(), 64);
            TLongSet mustRemove = new TLongHashSet();
            Collection<BaseEntity> mustAdd = new ArrayList<>();
            isViewing.forEach(id -> {
                Optional<BaseEntity> optional = getLevel().getEntityManager().findEntityById(id);
                if (optional.isPresent()) {
                    if (!inView.contains(optional.get())) {
                        mustRemove.add(id);
                    }
                } else {
                    mustRemove.add(id);
                }
                return true;
            });

            for (BaseEntity entity : inView) {
                if (entity.getEntityId() == getEntityId()) {
                    continue;
                }

                if (isViewing.add(entity.getEntityId())) {
                    mustAdd.add(entity);
                }
            }
            isViewing.removeAll(mustRemove);

            mustRemove.forEach(id -> {
                McpeRemoveEntity entity = new McpeRemoveEntity();
                entity.setEntityId(id);
                session.addToSendQueue(entity);
                return true;
            });

            for (BaseEntity entity : mustAdd) {
                session.addToSendQueue(entity.createAddEntityPacket());
            }
        }
    }

    @Nonnull
    @Override
    public UUID getUniqueId() {
        return session.getAuthenticationProfile().getIdentity();
    }

    @Override
    public boolean isXboxAuthenticated() {
        return session.getAuthenticationProfile().getXuid() != null;
    }

    @Nonnull
    @Override
    public OptionalLong getXuid() {
        return session.getAuthenticationProfile().getXuid() == null ? OptionalLong.empty() :
                OptionalLong.of(session.getAuthenticationProfile().getXuid());
    }

    @Nonnull
    @Override
    public String getName() {
        return session.getAuthenticationProfile().getDisplayName();
    }

    @Nonnull
    @Override
    public Optional<InetSocketAddress> getRemoteAddress() {
        return session.getRemoteAddress();
    }

    private void sendNewChunks() {
        getChunksForRadius(viewDistance, true).whenComplete((chunks, throwable) -> {
            if (throwable != null) {
                LOGGER.error("Unable to load chunks for " + getMcpeSession().getAuthenticationProfile().getDisplayName(), throwable);
                disconnect("Internal server error");
                return;
            }

            // Sort by whichever chunks are closest to the player for smoother loading
            Vector3f currentPosition = getPosition();
            int currentChunkX = currentPosition.getFloorX() >> 4;
            int currentChunkZ = currentPosition.getFloorZ() >> 4;
            chunks.sort(new AroundPointComparator(currentChunkX, currentChunkZ));

            for (Chunk chunk : chunks) {
                session.sendImmediatePackage(((VoxelwindChunk) chunk).getChunkDataPacket());
            }
        });
    }

    @Override
    public Skin getSkin() {
        return new Skin(session.getClientData().getSkinId(), session.getClientData().getSkinData());
    }

    @Nonnull
    @Override
    public GameMode getGameMode() {
        return gameMode;
    }

    @Override
    public void setGameMode(@Nonnull GameMode mode) {
        GameMode oldGameMode = gameMode;
        gameMode = Preconditions.checkNotNull(mode, "mode");

        if (oldGameMode != gameMode && spawned) {
            McpeSetPlayerGameMode packet = new McpeSetPlayerGameMode();
            packet.setGamemode(mode.ordinal());
            session.addToSendQueue(packet);
        }
    }

    @Override
    public void sendMessage(@Nonnull String message, @Nonnull PlayerMessageDisplayType type) {
        Preconditions.checkNotNull(message, "message");
        Preconditions.checkNotNull(type, "type");
        McpeText text = new McpeText();
        switch (type) {
            case CHAT:
                text.setType(McpeText.TextType.RAW);
                break;
            case TIP:
                text.setType(McpeText.TextType.TIP);
                break;
            case POPUP:
                text.setType(McpeText.TextType.POPUP);
                text.setSource(""); // TODO: Is it worth adding a caption for this?
                break;
        }
        text.setMessage(message);
        session.addToSendQueue(text);
    }

    @Override
    public void sendTranslatedMessage(@Nonnull TranslatedMessage message) {
        Preconditions.checkNotNull(message, "message");
        McpeText text = new McpeText();
        text.setType(McpeText.TextType.TRANSLATE);
        text.setTranslatedMessage(message);
        session.addToSendQueue(text);
    }

    @Override
    public void sendPopupMessage(@Nonnull PopupMessage message) {
        Preconditions.checkNotNull(message, "message");
        McpeText text = new McpeText();
        text.setType(McpeText.TextType.POPUP);
        text.setSource(message.getCaption());
        text.setMessage(message.getMessage());
        session.addToSendQueue(text);
    }

    @Override
    public PlayerInventory getInventory() {
        return playerInventory;
    }

    @Override
    public Optional<Inventory> getOpenedInventory() {
        return Optional.ofNullable(openedInventory);
    }

    @Override
    public void openInventory(Inventory inventory) {
        Preconditions.checkNotNull(inventory, "inventory");
        Preconditions.checkArgument(inventory instanceof VoxelwindBaseOpenableInventory, "inventory is not a valid type that can be opened");
        Preconditions.checkState(openedInventory == null, "inventory already opened");

        VoxelwindInventoryType internalType = VoxelwindInventoryType.fromApi(inventory.getInventoryType());
        byte windowId = internalType.getWindowId(this);
        openedInventory = inventory;
        openInventoryId = windowId;

        McpeContainerOpen openPacket = new McpeContainerOpen();
        openPacket.setWindowId(windowId);
        openPacket.setSlotCount((short) inventory.getInventoryType().getInventorySize());
        openPacket.setPosition(((OpenableInventory) inventory).getPosition());
        openPacket.setType(internalType.getType());
        session.addToSendQueue(openPacket);

        McpeContainerSetContents contents = new McpeContainerSetContents();
        contents.setWindowId(windowId);
        contents.setStacks(inventory.getAllContents());
        McpeBatch contentsBatch = new McpeBatch();
        contentsBatch.getPackages().add(contents);
        session.addToSendQueue(contentsBatch);

        ((VoxelwindBaseInventory) openedInventory).getObserverList().add(this);
    }

    @Override
    public void closeInventory() {
        Preconditions.checkState(openedInventory != null, "inventory not opened");
        McpeContainerClose close = new McpeContainerClose();
        close.setWindowId(openInventoryId);
        session.addToSendQueue(close);

        ((VoxelwindBaseInventory) openedInventory).getObserverList().remove(this);
        openedInventory = null;
        openInventoryId = -1;
    }

    public byte getNextWindowId() {
        return (byte) (1 + (windowIdGenerator.incrementAndGet() % 2));
    }

    public boolean isChunkInView(int x, int z) {
        return sentChunks.contains(new Vector2i(x, z));
    }

    @Override
    public void onInventoryChange(int slot, @Nullable ItemStack oldItem, @Nullable ItemStack newItem, VoxelwindBaseInventory inventory, @Nullable PlayerSession session) {
        byte windowId;
        if (inventory == openedInventory) {
            windowId = openInventoryId;
        } else if (inventory instanceof PlayerInventory) {
            windowId = 0x00;
        } else {
            return;
        }

        if (session != this) {
            McpeContainerSetSlot packet = new McpeContainerSetSlot();
            packet.setSlot((short) slot);
            packet.setStack(newItem);
            packet.setWindowId(windowId);
            this.session.addToSendQueue(packet);
        }
    }

    @Override
    public void onInventoryContentsReplacement(ItemStack[] newItems, VoxelwindBaseInventory inventory) {
        byte windowId;
        if (inventory == openedInventory) {
            windowId = openInventoryId;
        } else if (inventory instanceof PlayerInventory) {
            windowId = 0x00;
        } else {
            return;
        }

        McpeContainerSetContents packet = new McpeContainerSetContents();
        packet.setWindowId(windowId);
        packet.setStacks(newItems);
        McpeBatch contentsBatch = new McpeBatch();
        contentsBatch.getPackages().add(packet);
        session.addToSendQueue(contentsBatch);
    }

    private void sendPlayerInventory() {
        McpeContainerSetContents contents = new McpeContainerSetContents();
        contents.setWindowId((byte) 0x00);
        // Because MCPE is stupid, we have to add 9 more slots. The rest will be filled in as air.
        ItemStack[] stacks = Arrays.copyOf(playerInventory.getAllContents(), playerInventory.getInventoryType().getInventorySize() + 9);
        contents.setStacks(stacks);
        // Populate hotbar links.
        contents.setHotbarData(playerInventory.getHotbarLinks());
        McpeBatch contentsBatch = new McpeBatch();
        contentsBatch.getPackages().add(contents);
        session.sendImmediatePackage(contentsBatch);
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
            int radius = Math.max(5, Math.min(vwServer.getConfiguration().getMaximumViewDistance(), packet.getRadius()));
            McpeChunkRadiusUpdated updated = new McpeChunkRadiusUpdated();
            updated.setRadius(radius);
            session.sendImmediatePackage(updated);
            viewDistance = radius;

            getChunksForRadius(radius, true).whenComplete((chunks, throwable) -> {
                if (throwable != null) {
                    LOGGER.error("Unable to load chunks for " + getMcpeSession().getAuthenticationProfile().getDisplayName(), throwable);
                    disconnect("Internal server error");
                    return;
                }

                // Sort the chunks to be sent by whichever is closest to the spawn chunk for smoother loading.
                Vector3f spawnPosition = getPosition();
                int spawnChunkX = spawnPosition.getFloorX() >> 4;
                int spawnChunkZ = spawnPosition.getFloorZ() >> 4;
                chunks.sort(new AroundPointComparator(spawnChunkX, spawnChunkZ));

                for (Chunk chunk : chunks) {
                    session.sendImmediatePackage(((VoxelwindChunk) chunk).getChunkDataPacket());
                }

                if (!spawned) {
                    McpePlayStatus status = new McpePlayStatus();
                    status.setStatus(McpePlayStatus.Status.PLAYER_SPAWN);
                    session.sendImmediatePackage(status);

                    McpeSetTime setTime = new McpeSetTime();
                    setTime.setTime(getLevel().getTime());
                    setTime.setRunning(true);
                    session.sendImmediatePackage(setTime);

                    McpeRespawn respawn = new McpeRespawn();
                    respawn.setPosition(getPosition());
                    session.sendImmediatePackage(respawn);

                    updateViewableEntities();
                    sendAttributes();
                    sendPlayerInventory();

                    spawned = true;

                    PlayerJoinEvent event = new PlayerJoinEvent((Player) this, TextFormat.YELLOW + getName() + " joined the game.");
                    session.getServer().getEventManager().fire(event);
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
                    sendAttributes();
                    break;
                case ACTION_STOP_SPRINT:
                    sprinting = false;
                    sendAttributes();
                    break;
                case ACTION_START_SNEAK:
                    sneaking = true;
                    sendAttributes();
                    break;
                case ACTION_STOP_SNEAK:
                    sneaking = false;
                    sendAttributes();
                    break;
                case ACTION_SPAWN_OVERWORLD:
                    // Clean up attributes?
                    break;
                case ACTION_SPAWN_NETHER:
                    // Clean up attributes?
                    break;
            }

            McpeSetEntityData dataPacket = new McpeSetEntityData();
            dataPacket.getMetadata().put(0, getFlagValue());
            getLevel().getPacketManager().queuePacketForViewers(PlayerSession.this, dataPacket);
        }

        @Override
        public void handle(McpeAnimate packet) {
            getLevel().getPacketManager().queuePacketForPlayers(packet);
        }

        @Override
        public void handle(McpeText packet) {
            Preconditions.checkArgument(packet.getType() == McpeText.TextType.SOURCE, "Text packet type from client is not SOURCE");
            Preconditions.checkArgument(!packet.getMessage().contains("\0"), "Text packet from client contains a null byte");
            Preconditions.checkArgument(!packet.getMessage().trim().isEmpty(), "Text packet from client is effectively empty");

            // Debugging commands.
            if (packet.getMessage().startsWith("/")) {
                String command = packet.getMessage().substring(1);
                try {
                    session.getServer().getCommandManager().executeCommand(PlayerSession.this, command);
                } catch (CommandNotFoundException e) {
                    sendMessage(TextFormat.RED + "No such command found.");
                } catch (CommandException e) {
                    LOGGER.error("Error while running command '{}' for {}", command, getName(), e);
                    sendMessage(TextFormat.RED + "An error has occurred while running the command.");
                }
                return;
            }

            // By default, queue this packet for all players in the world.
            getLevel().getPacketManager().queuePacketForPlayers(packet);
        }

        @Override
        public void handle(McpeMovePlayer packet) {
            // TODO: We may do well to perform basic anti-cheat
            Vector3f originalPosition = getPosition();
            Vector3f newPosition = packet.getPosition().sub(0, 1.62, 0);

            // Reject moves that are obviously too fast. (>=100 blocks)
            if (newPosition.distanceSquared(newPosition) >= 10000) {
                setPosition(originalPosition);
                setRotation(packet.getRotation());
                return;
            }

            setPosition(newPosition);
            setRotation(packet.getRotation());

            // If we haven't moved in the X or Z axis, don't update viewable entities or try updating chunks - they haven't changed.
            if (hasSubstantiallyMoved(originalPosition, newPosition)) {
                updateViewableEntities();
                sendNewChunks();
            }
        }

        @Override
        public void handle(McpeContainerClose packet) {
            if (openedInventory != null) {
                ((VoxelwindBaseInventory) openedInventory).getObserverList().remove(PlayerSession.this);
                openedInventory = null;
                openInventoryId = -1;
            }
        }

        @Override
        public void handle(McpeContainerSetSlot packet) {
            VoxelwindBaseInventory window = null;
            if (openInventoryId < 0 || openInventoryId != packet.getWindowId()) {
                // There's no inventory open, so it's probably the player inventory.
                if (packet.getWindowId() == 0) {
                    window = playerInventory;
                } else if (packet.getWindowId() == 0x78) {
                    // It's the armor inventory. Handle it here.
                    switch (packet.getSlot()) {
                        case 0:
                            getEquipment().setHelmet(packet.getStack());
                            break;
                        case 1:
                            getEquipment().setChestplate(packet.getStack());
                            break;
                        case 2:
                            getEquipment().setLeggings(packet.getStack());
                            break;
                        case 3:
                            getEquipment().setBoots(packet.getStack());
                            break;
                    }
                    return;
                }
            } else {
                window = (VoxelwindBaseInventory) openedInventory;
            }

            if (window == null) {
                return;
            }

            window.setItem(packet.getSlot(), packet.getStack(), PlayerSession.this);
        }

        @Override
        public void handle(McpeMobEquipment packet) {
            // Basic sanity check:
            if (packet.getHotbarSlot() < 0 || packet.getHotbarSlot() >= 9) {
                throw new IllegalArgumentException("Specified hotbar slot " + packet.getHotbarSlot() + " isn't valid.");
            }

            int correctedInventorySlot = packet.getInventorySlot() - 9;
            int finalSlot = correctedInventorySlot < 0 || correctedInventorySlot >= playerInventory.getInventoryType().getInventorySize() ?
                    -1 : correctedInventorySlot;

            playerInventory.setHotbarLink(packet.getHotbarSlot(), finalSlot);
            playerInventory.setHeldHotbarSlot(packet.getHotbarSlot(), true);
        }

        @Override
        public void handle(McpeRemoveBlock packet) {
            // TODO: Perform sanity checks and drop items.
            int chunkX = packet.getPosition().getX() >> 4;
            int chunkZ = packet.getPosition().getZ() >> 4;

            Optional<Chunk> chunkOptional = getLevel().getChunkIfLoaded(chunkX, chunkZ);
            if (!chunkOptional.isPresent()) {
                // Chunk not loaded, danger ahead!
                LOGGER.error("{} tried to remove block at unloaded chunk ({}, {})", getName(), chunkX, chunkZ);
                return;
            }

            int inChunkX = packet.getPosition().getX() & 0x0f;
            int inChunkZ = packet.getPosition().getZ() & 0x0f;

            Block block = chunkOptional.get().getBlock(inChunkX, packet.getPosition().getY(), inChunkZ);
            // Call BlockReplaceEvent.
            BlockReplaceEvent event = new BlockReplaceEvent(block, block.getBlockState(), new BasicBlockState(BlockTypes.AIR, null, null),
                    PlayerSession.this, BlockReplaceEvent.ReplaceReason.PLAYER_BREAK);
            getServer().getEventManager().fire(event);
            if (event.getResult() == BlockReplaceEvent.Result.CONTINUE) {
                if (gameMode != GameMode.CREATIVE) {
                    BlockBehavior blockBehavior = BlockBehaviors.getBlockBehavior(block.getBlockState().getBlockType());
                    if (!blockBehavior.handleBreak(getServer(), PlayerSession.this, block, playerInventory.getStackInHand().orElse(null))) {
                        Collection<ItemStack> drops = blockBehavior.getDrops(getServer(), PlayerSession.this, block, playerInventory.getStackInHand().orElse(null));
                        for (ItemStack drop : drops) {
                            getLevel().dropItem(drop, block.getLevelLocation().toFloat().add(0.5, 0.5, 0.5));
                        }
                        chunkOptional.get().setBlock(inChunkX, packet.getPosition().getY(), inChunkZ, new BasicBlockState(BlockTypes.AIR, null, null));
                    }
                } else {
                    chunkOptional.get().setBlock(inChunkX, packet.getPosition().getY(), inChunkZ, new BasicBlockState(BlockTypes.AIR, null, null));
                }
            }

            getLevel().broadcastBlockUpdate(packet.getPosition());
        }

        @Override
        public void handle(McpeUseItem packet) {
            if (packet.getFace() == 0xff) {
                // TODO: Snowballs.
            } else if (packet.getFace() >= 0 && packet.getFace() <= 5) {
                // Sanity check:
                Optional<ItemStack> actuallyInHand = playerInventory.getStackInHand();
                LOGGER.info("Held: {}, slot: {}", actuallyInHand, playerInventory.getHeldHotbarSlot());
                if ((actuallyInHand.isPresent() && actuallyInHand.get().getItemType() != packet.getStack().getItemType()) ||
                        !actuallyInHand.isPresent() && packet.getStack().getItemType() == BlockTypes.AIR) {
                    // Not actually the same item.
                    return;
                }

                // What block is this item being used against?
                Optional<Block> usedAgainst = getLevel().getBlockIfChunkLoaded(packet.getLocation());
                if (!usedAgainst.isPresent()) {
                    // Not loaded into memory.
                    return;
                }

                // Ask the block being checked.
                ItemStack serverInHand = actuallyInHand.orElse(null);
                BlockFace face = BlockFace.values()[packet.getFace()];
                BlockBehavior againstBehavior = BlockBehaviors.getBlockBehavior(usedAgainst.get().getBlockState().getBlockType());
                switch (againstBehavior.handleItemInteraction(getServer(), PlayerSession.this, packet.getLocation(), face, serverInHand)) {
                    case NOTHING:
                        // Update inventory
                        sendPlayerInventory();
                        break;
                    case PLACE_BLOCK_AND_REMOVE_ITEM:
                        Preconditions.checkState(serverInHand != null && serverInHand.getItemType() instanceof BlockType, "Tried to place air or non-block.");
                        if (!BehaviorUtils.setBlockState(PlayerSession.this, packet.getLocation().add(face.getOffset()), BehaviorUtils.createBlockState(serverInHand))) {
                            sendPlayerInventory();
                            return;
                        }
                        // This will fall through
                    case REMOVE_ONE_ITEM:
                        if (serverInHand != null) {
                            int newItemAmount = serverInHand.getAmount() - 1;
                            if (newItemAmount <= 0) {
                                playerInventory.clearItem(playerInventory.getHeldInventorySlot());
                            } else {
                                playerInventory.setItem(playerInventory.getHeldInventorySlot(), serverInHand.toBuilder().amount(newItemAmount).build());
                            }
                        }
                        break;
                    case REDUCE_DURABILITY:
                        // TODO: Implement
                        break;
                }
            }
        }

        @Override
        public void handle(McpeDropItem packet) {
            // TODO: Events
            Optional<ItemStack> stackOptional = playerInventory.getStackInHand();
            if (!stackOptional.isPresent()) {
                sendPlayerInventory();
                return;
            }

            DroppedItem item = new VoxelwindDroppedItem(getPosition().add(0, 1.3, 0), getLevel(), getServer(), stackOptional.get());
            item.setMotion(getDirectionVector().mul(0.4));
            playerInventory.clearItem(playerInventory.getHeldInventorySlot());
        }
    }

    private void updatePlayerList() {
        synchronized (playersSentForList) {
            Set<Player> toAdd = new HashSet<>();
            Set<UUID> toRemove = new HashSet<>();
            Collection<Player> players = getServer().getAllOnlinePlayers();

            for (Player player : players) {
                if (playersSentForList.add(player.getUniqueId())) {
                    toAdd.add(player);
                }
            }

            for (UUID uuid : playersSentForList) {
                if (!players.stream().anyMatch(p -> p.getUniqueId().equals(uuid))) {
                    toRemove.add(uuid);
                }
            }

            if (!toAdd.isEmpty()) {
                McpePlayerList list = new McpePlayerList();
                list.setType((byte) 0);
                for (Player player : toAdd) {
                    McpePlayerList.Record record = new McpePlayerList.Record(player.getUniqueId());
                    record.setEntityId(player.getEntityId());
                    record.setSkin(player.getSkin());
                    record.setName(player.getName());
                    list.getRecords().add(record);
                }
                session.addToSendQueue(list);
            }

            if (!toRemove.isEmpty()) {
                playersSentForList.removeAll(toRemove);

                McpePlayerList list = new McpePlayerList();
                list.setType((byte) 1);
                for (UUID uuid : toRemove) {
                    list.getRecords().add(new McpePlayerList.Record(uuid));
                }
                session.addToSendQueue(list);
            }
        }
    }

    private static class AroundPointComparator implements Comparator<Chunk> {
        private final int spawnX;
        private final int spawnZ;

        private AroundPointComparator(int spawnX, int spawnZ) {
            this.spawnX = spawnX;
            this.spawnZ = spawnZ;
        }

        @Override
        public int compare(Chunk o1, Chunk o2) {
            // Use whichever is closest to the origin.
            return Integer.compare(distance(o1.getX(), o1.getZ()), distance(o2.getX(), o2.getZ()));
        }

        private int distance(int x, int z) {
            int dx = spawnX - x;
            int dz = spawnZ - z;
            return dx * dx + dz * dz;
        }
    }

    private static boolean hasSubstantiallyMoved(Vector3f oldPos, Vector3f newPos) {
        return (Float.compare(oldPos.getX(), newPos.getX()) != 0 || Float.compare(oldPos.getZ(), newPos.getZ()) != 0);
    }
}
