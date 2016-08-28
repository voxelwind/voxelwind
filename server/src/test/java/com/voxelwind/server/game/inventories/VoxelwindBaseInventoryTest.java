package com.voxelwind.server.game.inventories;

import com.voxelwind.api.game.inventories.InventoryType;
import com.voxelwind.api.game.item.ItemStack;
import com.voxelwind.api.game.level.block.BlockType;
import com.voxelwind.api.game.level.block.BlockTypes;
import com.voxelwind.server.game.item.VoxelwindItemStack;
import org.junit.Test;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.Assert.*;

public class VoxelwindBaseInventoryTest {
    @Test
    public void basicTest() throws Exception {
        VoxelwindBaseInventory inventory = new VoxelwindBaseInventory(InventoryType.CHEST);
        assertFalse("Inventory already has items in it", inventory.getItem(0).isPresent());

        inventory.setItem(0, new VoxelwindItemStack(BlockTypes.DIRT, 1, null));
        Optional<ItemStack> stack = inventory.getItem(0);
        assertTrue("Item did not get added", stack.isPresent());
        assertEquals("Item is invalid", BlockTypes.DIRT, stack.get().getItemType());

        inventory.clearItem(0);
        assertFalse("Inventory slot did not clear", inventory.getItem(0).isPresent());
    }

    @Test
    public void addItemDifferingItems() throws Exception {
        VoxelwindBaseInventory inventory = new VoxelwindBaseInventory(InventoryType.CHEST);
        assertTrue("Can't add item even when inventory is empty", inventory.addItem(new VoxelwindItemStack(BlockTypes.DIRT, 1, null)));
        assertTrue("Can't add item but no conflicts exist", inventory.addItem(new VoxelwindItemStack(BlockTypes.STONE, 1, null)));
    }

    @Test
    public void addItemCombiningItems() throws Exception {
        VoxelwindBaseInventory inventory = new VoxelwindBaseInventory(InventoryType.CHEST);
        assertTrue("Can't add item even when inventory is empty", inventory.addItem(new VoxelwindItemStack(BlockTypes.DIRT, 1, null)));
        assertEquals("Amount is invalid", 1, inventory.getItem(0).get().getAmount());
        assertTrue("Can't add item but stack has space", inventory.addItem(new VoxelwindItemStack(BlockTypes.DIRT, 1, null)));
        assertEquals("Amount was not changed", 2, inventory.getItem(0).get().getAmount());
    }

    @Test
    public void addItemRequiresOverflow() throws Exception {
        VoxelwindBaseInventory inventory = new VoxelwindBaseInventory(InventoryType.CHEST);
        assertTrue("Can't add item even when inventory is empty", inventory.addItem(new VoxelwindItemStack(BlockTypes.DIRT, 32, null)));
        assertEquals("Amount is invalid", 32, inventory.getItem(0).get().getAmount());
        assertTrue("Can't add item but stack has partial space and inventory is not full", inventory.addItem(new VoxelwindItemStack(BlockTypes.DIRT, 64, null)));
        assertEquals("Amount for first item was not changed", 64, inventory.getItem(0).get().getAmount());
        Optional<ItemStack> stack = inventory.getItem(1);
        assertTrue("Overflow item did not get added", stack.isPresent());
        assertEquals("Amount is invalid", 32, stack.get().getAmount());
    }

    @Test
    public void handleNothingTest() throws Exception {
        VoxelwindBaseInventory inventory = new VoxelwindBaseInventory(InventoryType.CHEST);
        inventory.setItem(0, new VoxelwindItemStack(BlockTypes.AIR, 0, null));
        assertFalse("Air block was added", inventory.getItem(0).isPresent());

        inventory.setItem(0, new VoxelwindItemStack(BlockTypes.DIRT, 1, null));
        assertTrue("Dirt block was not added", inventory.getItem(0).isPresent());

        inventory.setItem(0, new VoxelwindItemStack(BlockTypes.AIR, 0, null));
        assertFalse("Block was replaced with an air block", inventory.getItem(0).isPresent());
    }
}