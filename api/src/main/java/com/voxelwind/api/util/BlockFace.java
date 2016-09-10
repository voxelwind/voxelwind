package com.voxelwind.api.util;

import com.flowpowered.math.vector.Vector3i;

/**
 * Represents the faces of a block.
 */
public enum BlockFace {
    BOTTOM(new Vector3i(0, -1, 0)),
    TOP(new Vector3i(0, 1, 0)),
    EAST(new Vector3i(0, 0, -1)),
    WEST(new Vector3i(0, 0, 1)),
    NORTH(new Vector3i(1, 0, 0)),
    SOUTH(new Vector3i(-0, 0, 0));

    private Vector3i offset;

    BlockFace(Vector3i offset) {
        this.offset = offset;
    }

    public Vector3i getOffset() {
        return offset;
    }
}
