package com.voxelwind.server.game.item;

import com.google.common.base.Preconditions;
import com.voxelwind.api.game.item.ItemStack;
import com.voxelwind.api.game.item.ItemStackBuilder;
import com.voxelwind.api.game.item.Material;
import com.voxelwind.api.game.item.data.MaterialData;

import javax.annotation.Nonnull;

public class VoxelwindItemStackBuilder implements ItemStackBuilder {
    private Material material;
    private int amount = 1;
    private MaterialData data;

    @Override
    public ItemStackBuilder material(@Nonnull Material material) {
        Preconditions.checkNotNull(material, "material");
        this.material = material;
        this.data = null; // No data
        return this;
    }

    @Override
    public ItemStackBuilder amount(int amount) {
        Preconditions.checkArgument(amount >= 0 && amount <= 64, "Amount %s is not between 0 and 64", amount);
        this.amount = amount;
        return this;
    }

    @Override
    public ItemStackBuilder materialData(MaterialData data) {
        if (data != null) {
            Preconditions.checkState(material != null, "Material has not been set");
            Preconditions.checkArgument(data.getClass().isAssignableFrom(material.getMaterialDataClass()), "Material data is not valid (wanted %s)",
                    material.getMaterialDataClass().getName());
        }
        this.data = data;
        return this;
    }

    @Override
    public ItemStack build() {
        Preconditions.checkArgument(material != null, "Material has not been set");
        return new VoxelwindItemStack(material, amount, data);
    }
}
