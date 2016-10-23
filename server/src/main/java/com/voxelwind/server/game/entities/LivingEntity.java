package com.voxelwind.server.game.entities;

import com.flowpowered.math.vector.Vector3f;
import com.google.common.base.Preconditions;
import com.voxelwind.api.game.entities.Living;
import com.voxelwind.api.game.inventories.ArmorEquipment;
import com.voxelwind.api.server.Server;
import com.voxelwind.server.game.inventories.VoxelwindArmorEquipment;
import com.voxelwind.server.game.level.VoxelwindLevel;
import com.voxelwind.server.network.mcpe.packets.McpeEntityEvent;

public class LivingEntity extends BaseEntity implements Living {
    protected float drag = 0.02f;
    protected float gravity = 0.08f;
    private int health;
    private int maximumHealth;
    private final ArmorEquipment equipment;

    protected LivingEntity(EntityTypeData data, VoxelwindLevel level, Vector3f position, Server server, int maximumHealth) {
        super(data, position, level, server);
        this.maximumHealth = maximumHealth;
        this.health = maximumHealth;
        this.equipment = new VoxelwindArmorEquipment(this);
    }

    @Override
    public boolean onTick() {
        super.onTick();

        doMovement(drag, gravity);

        return true;
    }

    @Override
    public int getHealth() {
        return health;
    }

    @Override
    public void setHealth(int health) {
        checkIfAlive();

        Preconditions.checkArgument(health <= maximumHealth, "New health %s exceeds maximum health %s", health, maximumHealth);
        this.health = health;

        if (health <= 0) {
            doDeath();
        }
    }

    @Override
    public int getMaximumHealth() {
        return maximumHealth;
    }

    @Override
    public void setMaximumHealth(int maximumHealth) {
        Preconditions.checkArgument(health > 0, "New health %s is less than minimum allowed 1", maximumHealth);
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
