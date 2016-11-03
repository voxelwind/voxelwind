package com.voxelwind.server.game.item;

import com.voxelwind.api.game.item.ItemTypes;
import com.voxelwind.api.game.item.data.Coal;
import com.voxelwind.api.game.item.data.Dyed;
import com.voxelwind.api.game.level.block.BlockTypes;
import com.voxelwind.api.game.util.data.DyeColor;
import com.voxelwind.nbt.tags.CompoundTag;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class VoxelwindNBTUtilsTest {
    @Test
    public void createItemStack() throws Exception {
        VoxelwindItemStack stack1 = new VoxelwindItemStack(ItemTypes.APPLE, 14, null);
        VoxelwindItemStack stack2 = new VoxelwindItemStack(BlockTypes.WOOL, 14, Dyed.of(DyeColor.BLUE));
        VoxelwindItemStack stack3 = new VoxelwindItemStack(ItemTypes.COAL, 0, Coal.CHARCOAL, "test");

        CompoundTag out1 = stack1.toFullNBT();
        CompoundTag out2 = stack2.toFullNBT();
        CompoundTag out3 = stack3.toFullNBT();

        assertEquals(stack1, VoxelwindNBTUtils.createItemStack(out1.getValue()));
        assertEquals(stack2, VoxelwindNBTUtils.createItemStack(out2.getValue()));
        assertEquals(stack3, VoxelwindNBTUtils.createItemStack(out3.getValue()));
    }

}