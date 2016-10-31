package com.voxelwind.server.game.entities;

import com.flowpowered.math.vector.Vector3f;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.voxelwind.api.game.entities.Entity;
import com.voxelwind.api.game.entities.components.Component;
import com.voxelwind.api.game.entities.components.system.PhysicsSystem;
import com.voxelwind.api.game.entities.components.system.System;
import com.voxelwind.api.game.level.Level;
import com.voxelwind.api.server.Server;
import com.voxelwind.server.game.entities.components.PhysicsComponent;
import com.voxelwind.server.game.level.VoxelwindLevel;
import com.voxelwind.server.game.level.util.BoundingBox;
import com.voxelwind.server.network.NetworkPackage;
import com.voxelwind.server.network.mcpe.packets.McpeAddEntity;
import com.voxelwind.server.network.mcpe.util.metadata.EntityMetadataConstants;
import com.voxelwind.server.network.mcpe.util.metadata.MetadataDictionary;
import com.voxelwind.api.util.Rotation;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class BaseEntity implements Entity {
    private long entityId;
    private final Server server;
    private final EntityTypeData data;
    private VoxelwindLevel level;
    private Vector3f position;
    private Vector3f motion;
    private Rotation rotation;
    private boolean stale = true;
    private boolean teleported = false;
    protected boolean sprinting = false;
    protected boolean sneaking = false;
    private boolean invisible = false;
    private boolean removed = false;
    protected long tickCreated;
    private BoundingBox boundingBox;
    private final List<System> systems = new CopyOnWriteArrayList<>();
    private final Map<Class<? extends Component>, Component> componentMap = new HashMap<>();

    public BaseEntity(EntityTypeData data, Vector3f position, VoxelwindLevel level, Server server) {
        this.data = data;
        this.level = Preconditions.checkNotNull(level, "level");
        this.position = Preconditions.checkNotNull(position, "position");
        this.entityId = level.getEntityManager().allocateEntityId();
        this.server = server;
        this.rotation = Rotation.ZERO;
        this.motion = Vector3f.ZERO;
        this.level.getEntityManager().register(this);
        this.tickCreated = level.getCurrentTick();
        refreshBoundingBox();
    }

    @Override
    public long getEntityId() {
        return entityId;
    }

    @Nonnull
    @Override
    public VoxelwindLevel getLevel() {
        return level;
    }

    @Nonnull
    @Override
    public Vector3f getPosition() {
        return position;
    }

    @Nonnull
    @Override
    public Vector3f getGamePosition() {
        return getPosition().add(0, data.getHeight(), 0);
    }

    protected void setPosition(Vector3f position) {
        Preconditions.checkNotNull(position, "position");
        checkIfAlive();

        if (!this.position.equals(position)) {
            this.position = position;
            stale = true;

            refreshBoundingBox();
        }
    }

    @Override
    public void setPositionFromSystem(Vector3f position) {
        setPosition(position);
    }

    @Nonnull
    @Override
    public Rotation getRotation() {
        return rotation;
    }

    @Override
    public void setRotation(@Nonnull Rotation rotation) {
        Preconditions.checkNotNull(rotation, "rotation");
        checkIfAlive();

        if (!this.rotation.equals(rotation)) {
            this.rotation = rotation;
            stale = true;
        }
    }

    @Override
    public Vector3f getMotion() {
        return motion;
    }

    @Override
    public void setMotion(@Nonnull Vector3f motion) {
        Preconditions.checkNotNull(motion, "motion");
        checkIfAlive();

        if (!this.motion.equals(motion)) {
            this.motion = motion;
            stale = true;
        }
    }

    @Override
    public boolean isSprinting() {
        return sprinting;
    }

    @Override
    public void setSprinting(boolean sprinting) {
        checkIfAlive();

        if (this.sprinting != sprinting) {
            this.sprinting = sprinting;
            stale = true;
        }
    }

    @Override
    public boolean isSneaking() {
        return sneaking;
    }

    @Override
    public void setSneaking(boolean sneaking) {
        checkIfAlive();

        if (this.sneaking != sneaking) {
            this.sneaking = sneaking;
            stale = true;
        }
    }

    @Override
    public boolean isInvisible() {
        return invisible;
    }

    @Override
    public void setInvisible(boolean invisible) {
        checkIfAlive();

        if (this.invisible != invisible) {
            this.invisible = invisible;
            stale = true;
        }
    }

    @Override
    public Set<Class<? extends Component>> providedComponents() {
        // By default, entities don't provide any components.
        return ImmutableSet.copyOf(componentMap.keySet());
    }

    protected <C extends Component> void registerComponent(Class<C> clazz, C component) {
        componentMap.put(clazz, component);
    }

    @Override
    public <C extends Component> Optional<C> getComponent(@Nonnull Class<C> clazz) {
        Preconditions.checkNotNull(clazz, "clazz");
        return Optional.ofNullable((C) componentMap.get(clazz));
    }

    @Override
    public List<System> registeredSystems() {
        // This is a "weakly" unmodifiable list. We do not expect systems to be modified as often as, say, entities
        // are added into worlds, so using a copy-on-write list is an acceptable compromise.
        return Collections.unmodifiableList(systems);
    }

    @Override
    public void registerSystem(@Nonnull System system) {
        Preconditions.checkNotNull(system, "system");
        Preconditions.checkArgument(system.isSystemCompatible(this), "system is not compatible with this entity (wants %s, got %s)",
                system.getExpectedComponents(), providedComponents());
        systems.add(system);
    }

    @Override
    public void deregisterSystem(@Nonnull System system) {
        Preconditions.checkNotNull(system, "system");
        systems.remove(system);
    }

    public BoundingBox getBoundingBox() {
        return boundingBox;
    }

    private void refreshBoundingBox() {
        boundingBox = new BoundingBox(getPosition(), getPosition()).grow(data.getWidth() / 2, data.getLength() / 2, data.getWidth() / 2);
    }

    protected long getFlagValue() {
        BitSet set = new BitSet(64);
        // Fill with values
        set.set(EntityMetadataConstants.DATA_FLAGS_ON_FIRE, false); // Not implemented
        set.set(EntityMetadataConstants.DATA_FLAGS_SNEAKING, sneaking); // Sneaking
        set.set(EntityMetadataConstants.DATA_FLAGS_RIDING, false); // Riding (not implemented)
        set.set(EntityMetadataConstants.DATA_FLAGS_SPRINTING, sprinting); // Sprinting
        set.set(EntityMetadataConstants.DATA_FLAGS_INVISIBLE, invisible); // Invisible

        long[] array = set.toLongArray();
        return array.length == 0 ? 0 : array[0];
    }

    protected MetadataDictionary getMetadata() {
        checkIfAlive();

        // TODO: Implement more than this.
        MetadataDictionary dictionary = new MetadataDictionary();
        dictionary.put(EntityMetadataConstants.DATA_ENTITY_FLAGS, getFlagValue());
        dictionary.put(EntityMetadataConstants.DATA_NAMETAG, ""); // Not implemented
        dictionary.put(EntityMetadataConstants.DATA_HIDE_NAME_TAG, (byte) 0); // Not implemented
        dictionary.put(EntityMetadataConstants.DATA_MAYBE_AGE, 0); // Scale (not implemented)
        dictionary.put(EntityMetadataConstants.DATA_SCALE, 1f); // Scale (not implemented)
        dictionary.put(EntityMetadataConstants.DATA_MAX_AIR, (short) 20);
        dictionary.put(EntityMetadataConstants.DATA_COLLISION_BOX_HEIGHT, data.getHeight());
        dictionary.put(EntityMetadataConstants.DATA_COLLISION_BOX_WIDTH, data.getWidth());
        return dictionary;
    }

    public NetworkPackage createAddEntityPacket() {
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

    protected boolean isTeleported() {
        return teleported;
    }

    public void resetStale() {
        checkIfAlive();

        stale = false;
        teleported = false;
    }

    @Override
    public void teleport(@Nonnull Vector3f position) {
        teleport(level, position, rotation);
    }

    @Override
    public void teleport(@Nonnull Level level, @Nonnull Vector3f position) {
        teleport(level, position, rotation);
    }

    @Override
    public void teleport(@Nonnull Level level, @Nonnull Vector3f position, @Nonnull Rotation rotation) {
        Preconditions.checkNotNull(level, "level");
        Preconditions.checkNotNull(position, "position");
        Preconditions.checkNotNull(rotation, "rotation");
        Preconditions.checkArgument(level instanceof VoxelwindLevel, "Not a valid level.");
        Preconditions.checkArgument(level.getChunkIfLoadedForPosition(position.toInt()).isPresent(), "Position is in a chunk that is not loaded.");

        checkIfAlive();

        VoxelwindLevel oldLevel = this.level;
        if (oldLevel != level) {
            oldLevel.getEntityManager().unregister(this);
            ((VoxelwindLevel) level).getEntityManager().register(this);
            entityId = ((VoxelwindLevel) level).getEntityManager().allocateEntityId();
            tickCreated = level.getCurrentTick();

            // Mark as stale so that the destination level's entity manager will send the appropriate packets.
            this.stale = true;
        }
        this.level = (VoxelwindLevel) level;
        setPosition(position);
        setRotation(rotation);
        this.teleported = true;
    }

    @Override
    public void remove() {
        checkIfAlive();
        removed = true;
    }

    @Override
    public boolean isRemoved() {
        return removed;
    }

    @Override
    public Server getServer() {
        return server;
    }

    final void checkIfAlive() {
        Preconditions.checkState(!removed, "Entity has been removed.");
    }

    protected void setEntityId(long entityId) {
        this.entityId = entityId;
    }

    protected void doMovement() {
        doMovement(0.02f, 0.08f);
    }

    protected void doMovement(float drag, double gravity) {
        if (getMotion().lengthSquared() > 0) {
            boolean onGroundPreviously = isOnGround();
            setPosition(getPosition().add(getMotion()));
            boolean onGroundNow = isOnGround();

            if (!onGroundPreviously && onGroundNow) {
                setPosition(new Vector3f(getPosition().getX(), getPosition().getFloorY(), getPosition().getZ()));
                setMotion(Vector3f.ZERO);
            } else {
                setMotion(getMotion().mul(1f - drag));
                if (!onGroundNow) {
                    setMotion(getMotion().sub(0, gravity, 0));
                }
            }
        }
    }
}
