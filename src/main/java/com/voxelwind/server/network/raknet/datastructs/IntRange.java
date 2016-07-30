package com.voxelwind.server.network.raknet.datastructs;

import com.google.common.base.Preconditions;

import java.util.function.IntConsumer;

public final class IntRange {
    private final int start;
    private final int end;

    public IntRange(int num) {
        this(num, num);
    }

    public IntRange(int start, int end) {
        Preconditions.checkArgument(start <= end, "start is less than end");
        this.start = start;
        this.end = end;
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

    public void forEach(IntConsumer consumer) {
        for (int i = start; i <= end; i++) {
            consumer.accept(i);
        }
    }
}
