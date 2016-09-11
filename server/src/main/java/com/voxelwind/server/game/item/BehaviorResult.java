package com.voxelwind.server.game.item;

public enum BehaviorResult {
    /**
     * Nothing should happen.
     */
    NOTHING,
    /**
     * One item should be removed from the inventory.
     */
    REMOVE_ONE_ITEM,
    /**
     * The durability of the item should be reduced.
     */
    REDUCE_DURABILITY
}
