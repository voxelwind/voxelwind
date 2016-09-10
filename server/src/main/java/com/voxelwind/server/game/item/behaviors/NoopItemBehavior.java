package com.voxelwind.server.game.item.behaviors;

import com.flowpowered.math.vector.Vector3i;
import com.voxelwind.api.game.item.ItemStack;
import com.voxelwind.api.server.Player;
import com.voxelwind.api.server.Server;
import com.voxelwind.api.util.BlockFace;
import com.voxelwind.server.game.item.ItemBehavior;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * This behavior does nothing when the item is interacted with.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class NoopItemBehavior implements ItemBehavior {
    public static final NoopItemBehavior INSTANCE = new NoopItemBehavior();

    @Override
    public boolean handleItemInteraction(Server server, Player player, Vector3i against, BlockFace face, ItemStack withItem) {
        return true;
    }
}
