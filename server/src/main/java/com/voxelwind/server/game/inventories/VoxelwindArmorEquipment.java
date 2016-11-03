package com.voxelwind.server.game.inventories;

import com.voxelwind.api.game.entities.components.ArmorEquipment;
import com.voxelwind.api.game.item.ItemStack;

import javax.annotation.Nullable;
import java.util.Optional;

public class VoxelwindArmorEquipment implements ArmorEquipment {
    private ItemStack helmet;
    private ItemStack chestplate;
    private ItemStack leggings;
    private ItemStack boots;

    // TODO: These require parameter validation.
    @Override
    public Optional<ItemStack> getHelmet() {
        return Optional.ofNullable(helmet);
    }

    @Override
    public void setHelmet(@Nullable ItemStack stack) {
        helmet = stack;
    }

    @Override
    public Optional<ItemStack> getChestplate() {
        return Optional.ofNullable(chestplate);
    }

    @Override
    public void setChestplate(@Nullable ItemStack stack) {
        chestplate = stack;
    }

    @Override
    public Optional<ItemStack> getLeggings() {
        return Optional.ofNullable(leggings);
    }

    @Override
    public void setLeggings(@Nullable ItemStack stack) {
        leggings = stack;
    }

    @Override
    public Optional<ItemStack> getBoots() {
        return Optional.ofNullable(boots);
    }

    @Override
    public void setBoots(@Nullable ItemStack stack) {
        this.boots = stack;
    }
}
