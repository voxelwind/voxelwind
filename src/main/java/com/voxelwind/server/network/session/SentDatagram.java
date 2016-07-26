package com.voxelwind.server.network.session;

import com.google.common.base.Stopwatch;
import com.voxelwind.server.network.raknet.datagrams.EncapsulatedRakNetPacket;
import com.voxelwind.server.network.raknet.datagrams.RakNetDatagram;
import io.netty.util.ReferenceCountUtil;

import java.util.concurrent.TimeUnit;

class SentDatagram {
    private final RakNetDatagram datagram;
    private final Stopwatch stopwatch;
    private boolean released = false;

    SentDatagram(RakNetDatagram datagram) {
        this.datagram = datagram;
        this.stopwatch = Stopwatch.createStarted();
    }

    RakNetDatagram getDatagram() {
        return datagram;
    }

    public boolean isStale() {
        return stopwatch.elapsed(TimeUnit.SECONDS) >= 5;
    }

    void refreshForResend() {
        stopwatch.reset();
        for (EncapsulatedRakNetPacket packet : datagram.getPackets()) {
            packet.getBuffer().retain(); // because the re-write will cause a decrement of the reference count
        }
    }

    void tryRelease() {
        if (released) {
            return;
        }

        for (EncapsulatedRakNetPacket packet : datagram.getPackets()) {
            ReferenceCountUtil.safeRelease(packet.getBuffer());
        }

        stopwatch.stop();
        released = true;
    }
}
