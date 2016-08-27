package com.voxelwind.server.network.session;

import com.google.common.base.Preconditions;
import com.voxelwind.server.network.raknet.datagrams.EncapsulatedRakNetPacket;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;

import java.util.*;

public class SplitPacketHelper {
    private final Queue<EncapsulatedRakNetPacket> packets = new ArrayDeque<>();
    private final BitSet contained = new BitSet();
    private final long created = System.currentTimeMillis();
    private boolean released = false;

    public synchronized Optional<ByteBuf> add(EncapsulatedRakNetPacket packet) {
        Preconditions.checkNotNull(packet, "packet");
        Preconditions.checkArgument(packet.isHasSplit(), "packet is not split");
        Preconditions.checkState(!released, "packet has been released");

        if (contained.get(packet.getPartIndex())) {
            // Duplicate packet, ignore it.
            return Optional.empty();
        }

        contained.set(packet.getPartIndex(), true);
        packets.add(packet);

        for (int i = 0; i < packet.getPartCount(); i++) {
            if (!contained.get(i))
                return Optional.empty();
        }

        contained.clear();
        List<EncapsulatedRakNetPacket> sortedPackets = new ArrayList<>(packets);
        sortedPackets.sort((o1, o2) -> Integer.compare(o1.getPartIndex(), o2.getPartIndex()));

        ByteBuf buf = PooledByteBufAllocator.DEFAULT.directBuffer();
        for (EncapsulatedRakNetPacket netPacket : sortedPackets) {
            buf.writeBytes(netPacket.getBuffer());
        }

        release();
        packets.clear();

        return Optional.of(buf);
    }

    boolean expired() {
        // If we're waiting on a split packet for more than 30 seconds, the client on the other end is either severely
        // lagging, or has died.
        Preconditions.checkState(!released, "packet has been released");
        return System.currentTimeMillis() - created >= 30000;
    }

    void release() {
        Preconditions.checkState(!released, "packet has been released");

        for (EncapsulatedRakNetPacket packet : packets) {
            packet.release();
        }
    }
}
