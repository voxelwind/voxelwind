package com.voxelwind.nbt.tags;

/**
 * Represents the end of an NBT structure.
 */
public class EndTag implements Tag<Void> {
    public static final EndTag INSTANCE = new EndTag();

    private EndTag() {

    }

    public String getName() {
        return "";
    }

    public Void getValue() {
        return null;
    }
}
