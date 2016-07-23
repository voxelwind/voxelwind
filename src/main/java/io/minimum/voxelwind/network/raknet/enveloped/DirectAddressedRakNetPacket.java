package io.minimum.voxelwind.network.raknet.enveloped;

import io.minimum.voxelwind.network.raknet.RakNetPackage;
import io.netty.channel.DefaultAddressedEnvelope;

import java.net.InetSocketAddress;

public class DirectAddressedRakNetPacket extends DefaultAddressedEnvelope<RakNetPackage, InetSocketAddress> {
    public DirectAddressedRakNetPacket(RakNetPackage message, InetSocketAddress recipient, InetSocketAddress sender) {
        super(message, recipient, sender);
    }

    public DirectAddressedRakNetPacket(RakNetPackage message, InetSocketAddress recipient) {
        super(message, recipient);
    }
}
