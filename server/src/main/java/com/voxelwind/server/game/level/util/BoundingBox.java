package com.voxelwind.server.game.level.util;

import com.flowpowered.math.vector.Vector3f;
import com.flowpowered.math.vector.Vector3i;
import com.google.common.base.Preconditions;

public class BoundingBox {
    private final Vector3i start;
    private final Vector3i end;

    public BoundingBox(Vector3i start, Vector3i end) {
        Preconditions.checkNotNull(start, "start");
        Preconditions.checkNotNull(end, "end");
        this.start = start.min(end);
        this.end = end.max(start);
    }

    public Vector3i getStart() {
        return start;
    }

    public Vector3i getEnd() {
        return end;
    }

    public boolean isWithin(Vector3i vector) {
        Preconditions.checkNotNull(vector, "vector");
        return start.compareTo(vector) >= 0 && end.compareTo(vector) <= 0;
    }

    public boolean isWithin(Vector3f vector) {
        Preconditions.checkNotNull(vector, "vector");
        return isWithin(vector.toInt());
    }
}
