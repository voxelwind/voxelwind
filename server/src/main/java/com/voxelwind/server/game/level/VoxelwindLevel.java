package com.voxelwind.server.game.level;

import com.flowpowered.math.vector.Vector3f;
import com.flowpowered.math.vector.Vector3i;
import com.google.common.base.Preconditions;
import com.google.common.reflect.ClassPath;
import com.voxelwind.api.game.entities.Entity;
import com.voxelwind.api.game.entities.components.PickupDelay;
import com.voxelwind.api.game.entities.misc.DroppedItem;
import com.voxelwind.api.game.item.ItemStack;
import com.voxelwind.api.game.level.Chunk;
import com.voxelwind.api.game.level.Level;
import com.voxelwind.api.game.level.block.Block;
import com.voxelwind.api.game.level.block.BlockState;
import com.voxelwind.api.server.Server;
import com.voxelwind.nbt.tags.CompoundTag;
import com.voxelwind.nbt.tags.IntTag;
import com.voxelwind.server.VoxelwindServer;
import com.voxelwind.server.game.entities.BaseEntity;
import com.voxelwind.server.game.entities.EntitySpawner;
import com.voxelwind.server.game.entities.misc.VoxelwindDroppedItem;
import com.voxelwind.server.game.entities.visitor.EntityClassVisitor;
import com.voxelwind.server.game.level.manager.LevelChunkManager;
import com.voxelwind.server.game.level.manager.LevelEntityManager;
import com.voxelwind.server.game.level.manager.LevelPacketManager;
import com.voxelwind.server.game.level.provider.ChunkProvider;
import com.voxelwind.server.game.level.provider.LevelDataProvider;
import com.voxelwind.server.game.serializer.MetadataSerializer;
import com.voxelwind.server.network.mcpe.packets.McpeBlockEntityData;
import com.voxelwind.server.network.mcpe.packets.McpeUpdateBlock;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.ClassReader;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

public class VoxelwindLevel implements Level {
    private static final int FULL_TIME = 24000;
    private static final Logger LOGGER = LogManager.getLogger(VoxelwindLevel.class);
    private static final Map<Class<? extends Entity>, EntitySpawner> ENTITY_SPAWNER = new HashMap<>();

    static {
        // Scan the "com.voxelwind.server.game.entities" package for spawnable entities
        try {
            for (ClassPath.ClassInfo classInfo : ClassPath.from(VoxelwindServer.class.getClassLoader()).getTopLevelClassesRecursive("com.voxelwind.server.game.entities")) {
                ClassReader reader = new ClassReader(VoxelwindServer.class.getClassLoader().getResourceAsStream(classInfo.getResourceName()));
                EntityClassVisitor visitor = new EntityClassVisitor();
                reader.accept(visitor, ClassReader.SKIP_CODE | ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);

                Optional<String> classOptional = visitor.getEntityClass();
                if (classOptional.isPresent()) {
                    try {
                        Class<? extends Entity> spawnableClass = (Class<? extends Entity>) VoxelwindServer.class.getClassLoader().loadClass(classOptional.get());
                        ENTITY_SPAWNER.put(spawnableClass, new EntitySpawner((Class<? extends BaseEntity>) classInfo.load()));
                    } catch (ClassNotFoundException e) {
                        LOGGER.error("Found linked entity which is not in the classpath: " + classOptional.get());
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private final LevelChunkManager chunkManager;
    private final LevelDataProvider dataProvider;
    private final String name;
    private final UUID uuid;
    private final LevelEntityManager entityManager;
    private final LevelPacketManager packetManager;
    private long currentTick;
    private final Server server;

    public VoxelwindLevel(VoxelwindServer server, String name, ChunkProvider chunkProvider, LevelDataProvider dataProvider) {
        this.server = server;
        this.chunkManager = new LevelChunkManager(server, this, chunkProvider);
        this.name = name;
        this.uuid = UUID.randomUUID();
        this.entityManager = new LevelEntityManager(this);
        this.packetManager = new LevelPacketManager(this);
        this.dataProvider = dataProvider;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public UUID getUuid() {
        return uuid;
    }

    public LevelEntityManager getEntityManager() {
        return entityManager;
    }

    public LevelPacketManager getPacketManager() {
        return packetManager;
    }

    @Override
    public long getCurrentTick() {
        return currentTick;
    }

    @Override
    public Vector3f getSpawnLocation() {
        return dataProvider.getSpawnLocation();
    }

    @Override
    public int getTime() {
        return (int) ((currentTick + dataProvider.getSavedTime()) % FULL_TIME);
    }

    @Override
    public Optional<Chunk> getChunkIfLoaded(int x, int z) {
        return chunkManager.getChunkIfLoaded(x, z);
    }

    @Override
    public CompletableFuture<Chunk> getChunk(int x, int z) {
        return chunkManager.getChunk(x, z);
    }

    @Override
    @SuppressWarnings({"unchecked "})
    public <T extends Entity> CompletableFuture<T> spawn(@Nonnull Class<? extends Entity> klass, @Nonnull Vector3f position) {
        Preconditions.checkNotNull(klass, "klass");
        Preconditions.checkNotNull(position, "position");

        EntitySpawner entitySpawner;
        Preconditions.checkArgument((entitySpawner = ENTITY_SPAWNER.get(klass)) != null, "Entity class is not valid");

        CompletableFuture<T> future = new CompletableFuture<>();
        Vector3i vector3i = position.toInt();
        getChunk(vector3i.getX() >> 4, vector3i.getZ() >> 4).whenComplete((chunk, throwable) -> {
            if (throwable != null) {
                return;
            }

            future.complete(entitySpawner.spawnEntity(VoxelwindLevel.this, position, server));
        });

        return future;
    }

    @Override
    public DroppedItem dropItem(@Nonnull ItemStack stack, @Nonnull Vector3f position) {
        Preconditions.checkNotNull(stack, "stack");
        Preconditions.checkNotNull(position, "position");
        Preconditions.checkArgument(getBlockIfChunkLoaded(position.toInt()).isPresent(), "entities can not be spawned in unloaded chunks");
        return new VoxelwindDroppedItem(this, position, server, stack);
    }

    public void onTick() {
        currentTick++;

        /*if (currentTick % 200 == 0) {
            // Broadcast a time update
            McpeSetTime time = new McpeSetTime();
            time.setRunning(true);
            time.setTime(getTime());
            packetManager.queuePacketForPlayers(time);
        }*/

        entityManager.onTick();
        packetManager.onTick();

        if (currentTick % 20 == 0) {
            chunkManager.onTick();
        }
    }

    public void broadcastBlockUpdate(Vector3i position) {
        Optional<Block> blockOptional = getBlockIfChunkLoaded(position);
        if (!blockOptional.isPresent()) {
            LOGGER.error("Can't update for {} as chunk is not loaded", position);
            return;
        }

        BlockState state = blockOptional.get().getBlockState();
        McpeUpdateBlock packet = new McpeUpdateBlock();
        packet.setPosition(position);
        packet.setBlockId((byte) state.getBlockType().getId());
        short blockData = state.getBlockData() == null ? 0 : MetadataSerializer.serializeMetadata(state);
        packet.setMetadata((byte) (0xb << 4 | (blockData & 0xf)));
        packetManager.queuePacketForPlayers(packet);

        // Block entities may have changed too.
        if (state.getBlockEntity().isPresent()) {
            CompoundTag blockEntityTag = MetadataSerializer.serializeNBT(state);
            blockEntityTag.getValue().put("x", new IntTag("x", position.getX()));
            blockEntityTag.getValue().put("y", new IntTag("y", position.getY()));
            blockEntityTag.getValue().put("z", new IntTag("z", position.getZ()));

            McpeBlockEntityData packet2 = new McpeBlockEntityData();
            packet2.setPosition(position);
            packet2.setBlockEntityData(blockEntityTag);
            packetManager.queuePacketForPlayers(packet2);
        }
    }
}
