package io.minimum.voxelwind.network.session;

import com.google.common.base.Stopwatch;
import io.minimum.voxelwind.network.raknet.datagrams.EncapsulatedRakNetPacket;
import io.minimum.voxelwind.network.raknet.datagrams.RakNetDatagram;
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

    void refreshForResend(UserSession session) {
        stopwatch.reset();
        datagram.setDatagramSequenceNumber(session.getDatagramSequenceGenerator().incrementAndGet());
    }

    void tryRelease() {
        if (released) {
            return;
        }

        for (EncapsulatedRakNetPacket packet : datagram.getPackets()) {
            ReferenceCountUtil.safeRelease(packet.getBuffer());
        }

        stopwatch.stop();
    }
}
