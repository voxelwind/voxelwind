package com.voxelwind.api.game.inventories;

public enum InventoryType {
    CHEST(27),
    DOUBLE_CHEST(27 + 27),
    PLAYER(36),
    FURNACE(3),
    CRAFTING(5),
    WORKBENCH(10),
    BREWING_STAND(4),
    ANVIL(3),
    ENCHANTING(2),
    DISPENSER(9),
    DROPPER(9),
    HOPPER(5);

    private final int maximumSize;

    InventoryType(int maximumSize) {
        this.maximumSize = maximumSize;
    }

    public int getInventorySize() {
        return maximumSize;
    }
}
