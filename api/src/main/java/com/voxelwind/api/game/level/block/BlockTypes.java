package com.voxelwind.api.game.level.block;

/**
 * This class contains all block types recognized by Voxelwind and Pocket Edition.
 */
// TODO: Implement
public class BlockTypes {
    public static final BlockType AIR = getBlock(0);
    public static final BlockType STONE = getBlock(1);

    private static BlockType getBlock(int i) {
        return null;
    }

    private BlockTypes() {
        throw new AssertionError("Can't construct an instance of this class.");
    }

    public static BlockState forId(byte data) {
        return null;
    }
}
