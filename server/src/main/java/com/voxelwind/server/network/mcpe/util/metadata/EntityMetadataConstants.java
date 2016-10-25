package com.voxelwind.server.network.mcpe.util.metadata;

public class EntityMetadataConstants {
    public static final int DATA_TYPE_BYTE = 0;
    public static final int DATA_TYPE_SHORT = 1;
    public static final int DATA_TYPE_INT = 2;
    public static final int DATA_TYPE_FLOAT = 3;
    public static final int DATA_TYPE_STRING = 4;
    public static final int DATA_TYPE_SLOT = 5;
    public static final int DATA_TYPE_POS = 6;
    public static final int DATA_TYPE_LONG = 7;
    public static final int DATA_TYPE_VECTOR3F = 8;

    public static final int DATA_ENTITY_FLAGS = 0;
    public static final int DATA_VARIANT = 2; //int
    public static final int DATA_HIDE_NAME_TAG = 3; // byte
    public static final int DATA_NAMETAG = 4; //string
    public static final int DATA_OWNER_EID = 5; //long
    public static final int DATA_AIR = 7; //short
    public static final int DATA_POTION_COLOR = 8; //int (ARGB!)
    public static final int DATA_POTION_AMBIENT = 9; //byte
    public static final int DATA_EATING_HAYSTACK = 16;
    public static final int DATA_MAYBE_AGE = 25;
    public static final int DATA_LEAD_HOLDER_EID = 38; //long
    public static final int DATA_SCALE = 39;
    public static final int DATA_BUTTON_TEXT = 40; //string
    public static final int DATA_MAX_AIR = 44;
    public static final int DATA_COLLISION_BOX_HEIGHT = 53;
    public static final int DATA_COLLISION_BOX_WIDTH = 54;
    private EntityMetadataConstants() {

    }
}
