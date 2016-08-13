package com.voxelwind.server.level.util;

public class Attribute {
    private final String name;
    private final float minimumValue;
    private final float maximumValue;
    private final float value;

    public Attribute(String name, float minimumValue, float maximumValue, float value) {
        this.name = name;
        this.minimumValue = minimumValue;
        this.maximumValue = maximumValue;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public float getMinimumValue() {
        return minimumValue;
    }

    public float getMaximumValue() {
        return maximumValue;
    }

    public float getValue() {
        return value;
    }
}
