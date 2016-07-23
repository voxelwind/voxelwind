package io.minimum.voxelwind.network.raknet.datagrams;

import java.util.BitSet;

public class RakNetDatagramFlags {
    private final BitSet flags;

    public RakNetDatagramFlags(byte flag) {
        this.flags = BitSet.valueOf(new byte[]{ flag });
    }

    public boolean isValid() {
        return flags.get(7);
    }

    public boolean isAck() {
        return flags.get(6);
    }

    public boolean isNak() {
        return flags.get(5);
    }

    public boolean isPacketPair() {
        return !isNak() && flags.get(4);
    }

    public boolean isContinuousSend() {
        return !isNak() && flags.get(3);
    }

    public byte getFlagByte() {
        return flags.toByteArray()[0];
    }
}
