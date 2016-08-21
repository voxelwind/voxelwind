package com.voxelwind.server.game.item;

import com.voxelwind.api.game.item.ItemStack;
import com.voxelwind.api.game.item.ItemStackBuilder;
import com.voxelwind.api.game.item.Material;
import com.voxelwind.api.game.item.data.MaterialData;

import java.util.Optional;

public class VoxelwindItemStack implements ItemStack {
    private final Material material;
    private final int amount;
    private final MaterialData data;

    public VoxelwindItemStack(Material material, int amount, MaterialData data) {
        this.material = material;
        this.amount = amount;
        this.data = data;
    }

    @Override
    public Material getMaterial() {
        return material;
    }

    @Override
    public int getAmount() {
        return amount;
    }

    @Override
    public Optional<MaterialData> getMaterialData() {
        return Optional.ofNullable(data);
    }

    @Override
    public ItemStackBuilder toBuilder() {
        return new VoxelwindItemStackBuilder().material(material).amount(amount).materialData(data);
    }
}
