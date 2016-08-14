package com.voxelwind.server.level.entities;

import com.flowpowered.math.vector.Vector3f;
import com.flowpowered.math.vector.Vector3i;
import com.google.common.base.Preconditions;
import com.voxelwind.server.level.Level;
import com.voxelwind.server.level.chunk.Chunk;
import com.voxelwind.server.network.mcpe.packets.McpeAddEntity;
import com.voxelwind.server.network.mcpe.util.metadata.EntityMetadataConstants;
import com.voxelwind.server.network.mcpe.util.metadata.MetadataDictionary;
import com.voxelwind.server.util.Rotation;

import java.util.BitSet;
import java.util.Optional;

public class BaseEntity {
    private long entityId;
    private final EntityTypeData data;
    private Level level;
    private Vector3f position;
    private Vector3f motion;
    private Rotation rotation;
    private boolean stale = true;
    private boolean teleported = false;
    private boolean sprinting = false;
    private boolean sneaking = false;
    private boolean invisible = false;
    private boolean removed = false;

    public BaseEntity(EntityTypeData data, Vector3f position, Level level) {
        this.data = data;
        this.level = Preconditions.checkNotNull(level, "level");
        this.position = Preconditions.checkNotNull(position, "position");
        this.entityId = level.getEntityManager().allocateEntityId();
        this.rotation = Rotation.ZERO;
        this.motion = Vector3f.ZERO;
        this.level.getEntityManager().register(this);
    }

    protected static boolean isOnGround(Level level, Vector3f position) {
        Vector3i blockPosition = position.sub(0f, 0.1f, 0f).toInt();

        if (blockPosition.getY() < 0) {
            return false;
        }

        int chunkX = blockPosition.getX() >> 4;
        int chunkZ = blockPosition.getZ() >> 4;
        int chunkInX = blockPosition.getX() % 16;
        int chunkInZ = blockPosition.getZ() % 16;

        Optional<Chunk> chunkOptional = level.getChunkProvider().getIfLoaded(chunkX, chunkZ);
        return chunkOptional.isPresent() && chunkOptional.get().getBlock(chunkInX, blockPosition.getY(), chunkInZ) != 0;
    }

    public long getEntityId() {
        return entityId;
    }

    public Level getLevel() {
        return level;
    }

    public Vector3f getPosition() {
        return position;
    }

    public Vector3f getGamePosition() {
        return getPosition().add(0, data.getHeight(), 0);
    }

    protected void setPosition(Vector3f position) {
        checkIfAlive();

        if (!this.position.equals(position)) {
            this.position = position;
            stale = true;
        }
    }

    public Rotation getRotation() {
        return rotation;
    }

    public void setRotation(Rotation rotation) {
        checkIfAlive();

        if (!this.rotation.equals(rotation)) {
            this.rotation = rotation;
            stale = true;
        }
    }

    public Vector3f getMotion() {
        return motion;
    }

    public void setMotion(Vector3f motion) {
        checkIfAlive();

        if (!this.motion.equals(motion)) {
            this.motion = motion;
            stale = true;
        }
    }

    public boolean isSprinting() {
        return sprinting;
    }

    public void setSprinting(boolean sprinting) {
        checkIfAlive();

        if (this.sprinting != sprinting) {
            this.sprinting = sprinting;
            stale = true;
        }
    }

    public boolean isSneaking() {
        return sneaking;
    }

    public void setSneaking(boolean sneaking) {
        checkIfAlive();

        if (this.sneaking != sneaking) {
            this.sneaking = sneaking;
            stale = true;
        }
    }

    public boolean isInvisible() {
        return invisible;
    }

    public void setInvisible(boolean invisible) {
        checkIfAlive();

        if (this.invisible != invisible) {
            this.invisible = invisible;
            stale = true;
        }
    }

    public byte getFlagValue() {
        BitSet set = new BitSet(8);
        set.set(0, false); // On fire (not implemented)
        set.set(1, sneaking); // Sneaking
        set.set(2, false); // Riding (not implemented)
        set.set(3, sprinting); // Sprinting
        set.set(4, false); // In action(?)
        set.set(5, invisible); // Invisible

        byte[] array = set.toByteArray();
        return array.length == 0 ? 0 : array[0];
    }

    public MetadataDictionary getMetadata() {
        checkIfAlive();

        // TODO: Implement more than this.
        MetadataDictionary dictionary = new MetadataDictionary();
        dictionary.put(EntityMetadataConstants.DATA_FLAGS, getFlagValue());
        dictionary.put(EntityMetadataConstants.DATA_NAMETAG, ""); // Not implemented
        dictionary.put(EntityMetadataConstants.DATA_SHOW_NAMETAG, 0); // Not implemented
        dictionary.put(EntityMetadataConstants.DATA_SILENT, 0); // Not implemented
        dictionary.put(EntityMetadataConstants.DATA_POTION_COLOR, 0); // Not implemented
        dictionary.put(EntityMetadataConstants.DATA_POTION_AMBIENT, (byte) 0); // Not implemented
        dictionary.put(EntityMetadataConstants.DATA_NO_AI, (byte) 0); // Not implemented
        // Interesting flags:
        // 16 - player flags?
        // 23/24 - leads-related
        return dictionary;
    }

    public McpeAddEntity createAddEntityPacket() {
        checkIfAlive();

        McpeAddEntity packet = new McpeAddEntity();
        packet.setEntityId(getEntityId());
        packet.setEntityType(data.getType());
        packet.setPosition(getGamePosition());
        packet.setVelocity(getMotion());
        packet.setPitch(getRotation().getPitch());
        packet.setYaw(getRotation().getPitch());
        packet.getMetadata().putAll(getMetadata());
        return packet;
    }

    public boolean isStale() {
        return stale;
    }

    public boolean onTick() {
        if (removed) {
            // Remove the entity.
            return false;
        }

        // Continue ticking this entity
        return true;
    }

    public boolean isOnGround() {
        return isOnGround(level, position);
    }

    public boolean isTeleported() {
        return teleported;
    }

    public void resetStale() {
        checkIfAlive();

        stale = false;
        teleported = false;
    }

    public void teleport(Vector3f position) {
        teleport(level, position, rotation);
    }

    public void teleport(Level level, Vector3f position) {
        teleport(level, position, rotation);
    }

    public void teleport(Level level, Vector3f position, Rotation rotation) {
        checkIfAlive();

        Level oldLevel = this.level;
        if (oldLevel != level) {
            oldLevel.getEntityManager().unregister(this);
            level.getEntityManager().register(this);

            // Mark as stale so that the destination level's entity manager will send the appropriate packets.
            this.stale = true;
        }
        this.level = level;
        setPosition(position);
        setRotation(rotation);
        this.teleported = true;
    }

    public void remove() {
        checkIfAlive();
        removed = true;
    }

    public boolean isRemoved() {
        return removed;
    }

    protected final void checkIfAlive() {
        Preconditions.checkState(!removed, "Entity has been removed.");
    }
}
