package io.minimum.voxelwind.network.raknet.datagrams;

import io.minimum.voxelwind.network.raknet.RakNetUtil;
import io.minimum.voxelwind.network.session.UserSession;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;

import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

public class EncapsulatedRakNetPacket {
    private RakNetReliability reliability;
    private int reliabilityNumber;
    private int sequenceIndex;
    private int orderingIndex;
    private byte orderingChannel;
    private boolean hasSplit;
    private int partCount;
    private short partId;
    private int partIndex;
    private ByteBuf buffer;

    public RakNetReliability getReliability() {
        return reliability;
    }

    public void setReliability(RakNetReliability reliability) {
        this.reliability = reliability;
    }

    public int getReliabilityNumber() {
        return reliabilityNumber;
    }

    public void setReliabilityNumber(int reliabilityNumber) {
        this.reliabilityNumber = reliabilityNumber;
    }

    public int getSequenceIndex() {
        return sequenceIndex;
    }

    public void setSequenceIndex(int sequenceIndex) {
        this.sequenceIndex = sequenceIndex;
    }

    public int getOrderingIndex() {
        return orderingIndex;
    }

    public void setOrderingIndex(int orderingIndex) {
        this.orderingIndex = orderingIndex;
    }

    public byte getOrderingChannel() {
        return orderingChannel;
    }

    public void setOrderingChannel(byte orderingChannel) {
        this.orderingChannel = orderingChannel;
    }

    public boolean isHasSplit() {
        return hasSplit;
    }

    public void setHasSplit(boolean hasSplit) {
        this.hasSplit = hasSplit;
    }

    public int getPartCount() {
        return partCount;
    }

    public void setPartCount(int partCount) {
        this.partCount = partCount;
    }

    public short getPartId() {
        return partId;
    }

    public void setPartId(short partId) {
        this.partId = partId;
    }

    public int getPartIndex() {
        return partIndex;
    }

    public void setPartIndex(int partIndex) {
        this.partIndex = partIndex;
    }

    public void encode(ByteBuf buf) {
        int flags = reliability.ordinal();
        buf.writeByte((byte) ((flags << 5) | (hasSplit ? 0b00010000 : 0x00))); // flags
        buf.order(ByteOrder.LITTLE_ENDIAN).writeShort(buf.readableBytes() * 8); // size

        if (reliability == RakNetReliability.RELIABLE || reliability == RakNetReliability.RELIABLE_ORDERED ||
                reliability == RakNetReliability.RELIABLE_SEQUENCED || reliability == RakNetReliability.RELIABLE_WITH_ACK_RECEIPT ||
                reliability == RakNetReliability.RELIABLE_ORDERED_WITH_ACK_RECEIPT) {
            buf.order(ByteOrder.LITTLE_ENDIAN).writeMedium(reliabilityNumber);
        }

        if (reliability == RakNetReliability.UNRELIABLE_SEQUENCED || reliability == RakNetReliability.RELIABLE_SEQUENCED ||
                reliability == RakNetReliability.RELIABLE_ORDERED || reliability == RakNetReliability.RELIABLE_ORDERED_WITH_ACK_RECEIPT) {
            buf.order(ByteOrder.LITTLE_ENDIAN).writeMedium(orderingIndex);
            buf.writeByte(orderingChannel);
        }

        if (hasSplit) {
            buf.writeInt(partCount);
            buf.writeShort(partId);
            buf.writeInt(partIndex);
        }

        buf.writeBytes(buffer);
    }

    public void decode(ByteBuf buf) {
        short flags = buf.readUnsignedByte();
        reliability = RakNetReliability.values()[((flags & 0b11100000) >> 5)];
        hasSplit = ((flags & 0b00010000) > 0);
        short size = (short) Math.ceil(buf.readShort() / 8D);

        if (reliability == RakNetReliability.RELIABLE || reliability == RakNetReliability.RELIABLE_ORDERED ||
                reliability == RakNetReliability.RELIABLE_SEQUENCED || reliability == RakNetReliability.RELIABLE_WITH_ACK_RECEIPT ||
                reliability == RakNetReliability.RELIABLE_ORDERED_WITH_ACK_RECEIPT) {
            reliabilityNumber = buf.order(ByteOrder.LITTLE_ENDIAN).readMedium();
        }

        if (reliability == RakNetReliability.UNRELIABLE_SEQUENCED || reliability == RakNetReliability.RELIABLE_SEQUENCED ||
                reliability == RakNetReliability.RELIABLE_ORDERED || reliability == RakNetReliability.RELIABLE_ORDERED_WITH_ACK_RECEIPT) {
            orderingIndex = buf.order(ByteOrder.LITTLE_ENDIAN).readMedium();
            orderingChannel = buf.readByte();
        }

        if (hasSplit) {
            partCount = buf.readInt();
            partId = buf.readShort();
            partIndex = buf.readInt();
        }

        buffer = buf.readSlice(size);
    }

    public static List<EncapsulatedRakNetPacket> encapsulatePackage(ByteBuf buffer, UserSession session) {
        // Potentially split the package..
        List<ByteBuf> bufs = new ArrayList<>();
        int by = session.getMtu() - 100; // TODO: This could be lowered to as little as 24, but needs to be checked.
        if (buffer.readableBytes() > by) { // accounting for bookkeeping
            // Split the buffer up
            int split = (int) Math.ceil(buffer.readableBytes() / by);
            for (int i = 0; i < split; i++) {
                bufs.add(buffer.slice(i * by, Math.min(buffer.readableBytes(), (i + 1) * by)));
            }
        } else {
            bufs.add(buffer);
        }

        // Now create the packets.
        List<EncapsulatedRakNetPacket> packets = new ArrayList<>();
        short splitId = (short) (System.nanoTime() % Short.MAX_VALUE);
        int orderNumber = session.isEncrypted() ? session.getOrderSequenceGenerator().getAndIncrement() : 0;
        for (int i = 0; i < bufs.size(); i++) {
            // NB: When we add encryption support, you must use RELIABLE_ORDERED
            EncapsulatedRakNetPacket packet = new EncapsulatedRakNetPacket();
            packet.setBuffer(bufs.get(i));
            packet.setReliability(session.isEncrypted() ? RakNetReliability.RELIABLE_ORDERED : RakNetReliability.RELIABLE);
            packet.setReliabilityNumber(session.getReliabilitySequenceGenerator().getAndIncrement());
            packet.setOrderingIndex(orderNumber);
            if (bufs.size() > 1) {
                packet.setHasSplit(true);
                packet.setPartIndex(i);
                packet.setPartCount(bufs.size());
                packet.setPartId(splitId);
            }
            packets.add(packet);
        }
        return packets;
    }

    public ByteBuf getBuffer() {
        return buffer;
    }

    public void setBuffer(ByteBuf buffer) {
        this.buffer = buffer;
    }

    public int totalLength() {
        // Back of the envelope calculation, YMMV
        return buffer.writerIndex() + 24;
    }

    @Override
    public String toString() {
        return "EncapsulatedRakNetPacket{" +
                "reliability=" + reliability +
                ", reliabilityNumber=" + reliabilityNumber +
                ", sequenceIndex=" + sequenceIndex +
                ", orderingIndex=" + orderingIndex +
                ", orderingChannel=" + orderingChannel +
                ", hasSplit=" + hasSplit +
                ", partCount=" + partCount +
                ", partId=" + partId +
                ", partIndex=" + partIndex +
                ", buffer=" + buffer +
                ", totalLength=" + totalLength() +
                '}';
    }
}
