package com.voxelwind.server.network.raknet.enveloped;

import com.voxelwind.server.network.NetworkPackage;
import io.netty.channel.DefaultAddressedEnvelope;

import java.net.InetSocketAddress;

public class DirectAddressedRakNetPacket extends DefaultAddressedEnvelope<NetworkPackage, InetSocketAddress> {
    public DirectAddressedRakNetPacket(NetworkPackage message, InetSocketAddress recipient, InetSocketAddress sender) {
        super(message, recipient, sender);
    }

    public DirectAddressedRakNetPacket(NetworkPackage message, InetSocketAddress recipient) {
        super(message, recipient);
    }
}
