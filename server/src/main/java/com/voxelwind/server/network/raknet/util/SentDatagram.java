package com.voxelwind.server.network.raknet.util;

import com.google.common.base.Preconditions;
import com.google.common.base.Stopwatch;
import com.voxelwind.server.network.raknet.datagrams.RakNetDatagram;

import java.util.concurrent.TimeUnit;

public class SentDatagram {
    private final RakNetDatagram datagram;
    private final Stopwatch stopwatch;
    private boolean released = false;

    public SentDatagram(RakNetDatagram datagram) {
        this.datagram = datagram;
        this.stopwatch = Stopwatch.createStarted();
    }

    public RakNetDatagram getDatagram() {
        return datagram;
    }

    public boolean isStale() {
        return stopwatch.elapsed(TimeUnit.SECONDS) >= 5;
    }

    public void refreshForResend() {
        Preconditions.checkState(!released, "Already released");
        stopwatch.reset();
        stopwatch.start();
        datagram.retain();
    }

    public void tryRelease() {
        if (released) {
            return;
        }

        datagram.release();
        stopwatch.stop();
        released = true;
    }
}
