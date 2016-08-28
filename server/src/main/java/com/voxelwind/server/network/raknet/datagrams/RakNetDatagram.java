package com.voxelwind.server.network.raknet.datagrams;

import io.netty.buffer.ByteBuf;
import io.netty.util.AbstractReferenceCounted;
import io.netty.util.ReferenceCountUtil;

import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RakNetDatagram extends AbstractReferenceCounted {
    private final List<EncapsulatedRakNetPacket> packets = new ArrayList<>();
    private RakNetDatagramFlags flags = new RakNetDatagramFlags((byte) 0x84);
    private int datagramSequenceNumber;

    @Override
    public RakNetDatagram retain() {
        super.retain();
        return this;
    }

    @Override
    public RakNetDatagram retain(int increment) {
        super.retain(increment);
        return this;
    }

    public void decode(ByteBuf buf) {
        flags = new RakNetDatagramFlags(buf.readByte());
        datagramSequenceNumber = buf.order(ByteOrder.LITTLE_ENDIAN).readMedium();
        while (buf.isReadable()) {
            EncapsulatedRakNetPacket packet = new EncapsulatedRakNetPacket();
            packet.decode(buf);
            packets.add(packet);
        }
    }

    public void encode(ByteBuf buf) {
        buf.writeByte(flags.getFlagByte());
        buf.order(ByteOrder.LITTLE_ENDIAN).writeMedium(datagramSequenceNumber);
        for (EncapsulatedRakNetPacket packet : packets) {
            packet.encode(buf);
        }
    }

    public RakNetDatagramFlags getFlags() {
        return flags;
    }

    public void setFlags(RakNetDatagramFlags flags) {
        this.flags = flags;
    }

    public int getDatagramSequenceNumber() {
        return datagramSequenceNumber;
    }

    public void setDatagramSequenceNumber(int datagramSequenceNumber) {
        this.datagramSequenceNumber = datagramSequenceNumber;
    }

    public List<EncapsulatedRakNetPacket> getPackets() {
        return Collections.unmodifiableList(packets);
    }

    public boolean tryAddPacket(EncapsulatedRakNetPacket packet, short mtu) {
        int packetLn = packet.totalLength();
        if (packetLn >= mtu - 4) {
            return false; // Packet is too large
        }

        int existingLn = 0;
        for (EncapsulatedRakNetPacket netPacket : getPackets()) {
            existingLn += netPacket.totalLength();
        }

        if (existingLn + packetLn >= mtu - 4) {
            return false;
        }

        packets.add(packet);
        if (packet.isHasSplit()) {
            flags = new RakNetDatagramFlags((byte) 0x8c); // set continuous send
        }
        return true;
    }

    @Override
    public String toString() {
        return "RakNetDatagram{" +
                "flags=" + flags +
                ", datagramSequenceNumber=" + datagramSequenceNumber +
                ", packets=" + packets +
                '}';
    }

    @Override
    protected void deallocate() {
        for (EncapsulatedRakNetPacket packet : packets) {
            ReferenceCountUtil.release(packet);
        }
    }
}
