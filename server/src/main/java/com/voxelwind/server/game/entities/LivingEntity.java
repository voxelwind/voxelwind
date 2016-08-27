package com.voxelwind.server.game.entities;

import com.flowpowered.math.vector.Vector3f;
import com.google.common.base.Preconditions;
import com.voxelwind.api.game.entities.Living;
import com.voxelwind.api.game.inventories.ArmorEquipment;
import com.voxelwind.api.game.inventories.Inventory;
import com.voxelwind.server.game.inventories.VoxelwindArmorEquipment;
import com.voxelwind.server.game.level.VoxelwindLevel;
import com.voxelwind.server.network.mcpe.packets.McpeEntityEvent;

public class LivingEntity extends BaseEntity implements Living {
    protected float drag = 0.02f;
    protected float gravity = 0.08f;
    private float health;
    private float maximumHealth;
    private final ArmorEquipment equipment;

    public LivingEntity(EntityTypeData data, VoxelwindLevel level, Vector3f position, float maximumHealth) {
        super(data, position, level);
        this.maximumHealth = maximumHealth;
        this.health = maximumHealth;
        this.equipment = new VoxelwindArmorEquipment(this);
    }

    @Override
    public boolean onTick() {
        super.onTick();

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

        return true;
    }

    @Override
    public float getHealth() {
        return health;
    }

    @Override
    public void setHealth(float health) {
        checkIfAlive();

        Preconditions.checkArgument(Float.compare(health, maximumHealth) <= 0, "New health %s exceeds maximum health %s", health, maximumHealth);
        this.health = health;

        if (Double.compare(health, maximumHealth) <= 0) {
            doDeath();
        }
    }

    @Override
    public float getMaximumHealth() {
        return maximumHealth;
    }

    @Override
    public void setMaximumHealth(float maximumHealth) {
        Preconditions.checkArgument(Float.compare(maximumHealth, 0) <= 0, "New health %s is less than minimum allowed 0", maximumHealth);
        this.maximumHealth = maximumHealth;
        this.health = Math.min(maximumHealth, health);
    }

    protected void doDeath() {
        McpeEntityEvent event = new McpeEntityEvent();
        event.setEntityId(getEntityId());
        event.setEvent((byte) 3);
        getLevel().getPacketManager().queuePacketForViewers(this, event);

        // Technically, the entity will live for one extra tick, but that shouldn't matter.
        remove();
    }

    public void sendUpdateArmorPacket() {

    }

    @Override
    public ArmorEquipment getEquipment() {
        return equipment;
    }
}
