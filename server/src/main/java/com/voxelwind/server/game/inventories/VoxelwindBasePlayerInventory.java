package com.voxelwind.server.game.inventories;

import com.voxelwind.api.game.inventories.PlayerInventory;
import com.voxelwind.api.game.item.ItemStack;
import com.voxelwind.server.network.session.PlayerSession;

import javax.annotation.Nullable;
import java.util.Optional;

public class VoxelwindBasePlayerInventory extends VoxelwindBaseInventory implements PlayerInventory {
    private final PlayerSession session;
    private final VoxelwindArmorEquipment armorEquipment;

    public VoxelwindBasePlayerInventory(PlayerSession session) {
        // TODO: Verify
        super(45);
        this.session = session;
        this.armorEquipment = new VoxelwindArmorEquipment(session);
    }

    @Override
    public Optional<ItemStack> getHelmet() {
        return armorEquipment.getHelmet();
    }

    @Override
    public void setHelmet(@Nullable ItemStack stack) {
        armorEquipment.setHelmet(stack);
    }

    @Override
    public Optional<ItemStack> getChestplate() {
        return armorEquipment.getChestplate();
    }

    @Override
    public void setChestplate(@Nullable ItemStack stack) {
        armorEquipment.setChestplate(stack);
    }

    @Override
    public Optional<ItemStack> getLeggings() {
        return armorEquipment.getLeggings();
    }

    @Override
    public void setLeggings(@Nullable ItemStack stack) {
        armorEquipment.setLeggings(stack);
    }

    @Override
    public Optional<ItemStack> getBoots() {
        return armorEquipment.getBoots();
    }

    @Override
    public void setBoots(@Nullable ItemStack stack) {
        armorEquipment.setBoots(stack);
    }
}
