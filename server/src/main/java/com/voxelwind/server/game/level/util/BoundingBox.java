package com.voxelwind.server.game.level.util;

import com.flowpowered.math.vector.Vector3f;
import com.flowpowered.math.vector.Vector3i;
import com.google.common.base.Preconditions;
import lombok.Value;

@Value
public class BoundingBox {
    private final Vector3f start;
    private final Vector3f end;

    public BoundingBox(Vector3f start, Vector3f end) {
        Preconditions.checkNotNull(start, "start");
        Preconditions.checkNotNull(end, "end");
        this.start = start.min(end);
        this.end = end.max(start);
    }

    public boolean isWithin(Vector3i vector) {
        Preconditions.checkNotNull(vector, "vector");
        return isWithin(vector.toFloat());
    }

    public boolean isWithin(Vector3f vector) {
        Preconditions.checkNotNull(vector, "vector");
        return Float.compare(vector.getX(), start.getX()) >= 0 &&
                Float.compare(vector.getX(), end.getX()) <= 0 &&
                Float.compare(vector.getY(), start.getY()) >= 0 &&
                Float.compare(vector.getY(), end.getY()) <= 0 &&
                Float.compare(vector.getZ(), start.getZ()) >= 0 &&
                Float.compare(vector.getZ(), end.getZ()) <= 0;
    }
}
