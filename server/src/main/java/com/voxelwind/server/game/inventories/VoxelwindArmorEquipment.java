package com.voxelwind.server.game.inventories;

import com.voxelwind.api.game.inventories.ArmorEquipment;
import com.voxelwind.api.game.item.ItemStack;
import com.voxelwind.server.game.entities.LivingEntity;

import javax.annotation.Nullable;
import java.util.Optional;

public class VoxelwindArmorEquipment implements ArmorEquipment {
    private final LivingEntity session;
    private ItemStack helmet;
    private ItemStack chestplate;
    private ItemStack leggings;
    private ItemStack boots;

    public VoxelwindArmorEquipment(LivingEntity session) {
        this.session = session;
    }

    // TODO: These require parameter validation.
    @Override
    public Optional<ItemStack> getHelmet() {
        return Optional.ofNullable(helmet);
    }

    @Override
    public void setHelmet(@Nullable ItemStack stack) {
        helmet = stack;
        session.sendUpdateArmorPacket();
    }

    @Override
    public Optional<ItemStack> getChestplate() {
        return Optional.ofNullable(chestplate);
    }

    @Override
    public void setChestplate(@Nullable ItemStack stack) {
        chestplate = stack;
        session.sendUpdateArmorPacket();
    }

    @Override
    public Optional<ItemStack> getLeggings() {
        return Optional.ofNullable(leggings);
    }

    @Override
    public void setLeggings(@Nullable ItemStack stack) {
        leggings = stack;
        session.sendUpdateArmorPacket();
    }

    @Override
    public Optional<ItemStack> getBoots() {
        return Optional.ofNullable(boots);
    }

    @Override
    public void setBoots(@Nullable ItemStack stack) {
        this.boots = stack;
        session.sendUpdateArmorPacket();
    }
}
