package com.voxelwind.server.network.mcpe;

import com.flowpowered.math.vector.Vector3f;
import com.flowpowered.math.vector.Vector3i;
import com.google.common.base.Preconditions;
import com.voxelwind.api.game.item.ItemStack;
import com.voxelwind.api.game.item.ItemStackBuilder;
import com.voxelwind.api.game.item.ItemType;
import com.voxelwind.api.game.item.ItemTypes;
import com.voxelwind.api.game.level.block.BlockTypes;
import com.voxelwind.api.server.Skin;
import com.voxelwind.api.server.player.TranslatedMessage;
import com.voxelwind.api.util.Rotation;
import com.voxelwind.nbt.io.NBTReader;
import com.voxelwind.nbt.io.NBTWriter;
import com.voxelwind.nbt.tags.CompoundTag;
import com.voxelwind.nbt.tags.Tag;
import com.voxelwind.nbt.util.Varints;
import com.voxelwind.server.game.item.VoxelwindItemStack;
import com.voxelwind.server.game.item.VoxelwindItemStackBuilder;
import com.voxelwind.server.game.item.VoxelwindNBTUtils;
import com.voxelwind.server.game.level.util.Attribute;
import com.voxelwind.server.game.serializer.MetadataSerializer;
import com.voxelwind.server.network.mcpe.util.ResourcePackInfo;
import com.voxelwind.server.network.raknet.RakNetUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.ByteBufUtil;
import io.netty.util.CharsetUtil;

import java.io.IOException;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class McpeUtil {
    private McpeUtil() {

    }

    public static void writeVarintLengthString(ByteBuf buffer, String string) {
        Preconditions.checkNotNull(buffer, "buffer");
        Preconditions.checkNotNull(string, "string");
        byte[] bytes = string.getBytes(CharsetUtil.UTF_8);
        Varints.encodeUnsigned(buffer, bytes.length);
        buffer.writeBytes(bytes);
    }

    public static String readVarintLengthString(ByteBuf buffer) {
        Preconditions.checkNotNull(buffer, "buffer");
        int length = Varints.decodeUnsigned(buffer);
        byte[] readBytes = new byte[length];
        buffer.readBytes(readBytes);
        return new String(readBytes, StandardCharsets.UTF_8);
    }

    public static void writeLELengthString(ByteBuf buffer, String string) {
        Preconditions.checkNotNull(buffer, "buffer");
        Preconditions.checkNotNull(string, "string");
        buffer.order(ByteOrder.LITTLE_ENDIAN).writeInt(string.length());
        ByteBufUtil.writeUtf8(buffer, string);
    }

    public static String readLELengthString(ByteBuf buffer) {
        Preconditions.checkNotNull(buffer, "buffer");

        int length = (buffer.order(ByteOrder.LITTLE_ENDIAN).readInt());
        byte[] bytes = new byte[length];
        buffer.readBytes(bytes);
        return new String(bytes, StandardCharsets.UTF_8);
    }

    public static void writeBlockCoords(ByteBuf buf, Vector3i vector3i) {
        Varints.encodeSigned(buf, vector3i.getX());
        buf.writeByte(vector3i.getY());
        Varints.encodeSigned(buf, vector3i.getZ());
    }

    public static Vector3i readBlockCoords(ByteBuf buf) {
        int x = Varints.decodeSigned(buf);
        int y = buf.readByte();
        int z = Varints.decodeSigned(buf);
        return new Vector3i(x, y, z);
    }

    public static void writeVector3f(ByteBuf buf, Vector3f vector3f) {
        ByteBuf leBuf = buf.order(ByteOrder.LITTLE_ENDIAN);
        leBuf.writeFloat(vector3f.getX());
        leBuf.writeFloat(vector3f.getY());
        leBuf.writeFloat(vector3f.getZ());
    }

    public static Vector3f readVector3f(ByteBuf buf) {
        ByteBuf leBuf = buf.order(ByteOrder.LITTLE_ENDIAN);
        double x = leBuf.readFloat();
        double y = leBuf.readFloat();
        double z = leBuf.readFloat();
        return new Vector3f(x, y, z);
    }

    public static Collection<Attribute> readAttributes(ByteBuf buf) {
        List<Attribute> attributes = new ArrayList<>();
        int size = Varints.decodeUnsigned(buf);

        for (int i = 0; i < size; i++) {
            float min = buf.readFloat();
            float max = buf.readFloat();
            float val = buf.readFloat();
            float defaultVal = buf.readFloat();
            String name = readVarintLengthString(buf);

            attributes.add(new Attribute(name, min, max, val, defaultVal));
        }

        return attributes;
    }

    public static void writeAttributes(ByteBuf buf, Collection<Attribute> attributeList) {
        Varints.encodeUnsigned(buf, attributeList.size());
        for (Attribute attribute : attributeList) {
            buf.writeFloat(attribute.getMinimumValue());
            buf.writeFloat(attribute.getMaximumValue());
            buf.writeFloat(attribute.getValue());
            buf.writeFloat(attribute.getDefaultValue());
            writeVarintLengthString(buf, attribute.getName());
        }
    }

    public static Skin readSkin(ByteBuf buf) {
        String type = RakNetUtil.readString(buf);
        short length = buf.readShort();
        if (length == 64*32*4 || length == 64*64*4) {
            byte[] in = new byte[length];
            buf.readBytes(in);

            return new Skin(type, in);
        }

        return new Skin("Standard_Custom", new byte[0]);
    }

    public static void writeSkin(ByteBuf buf, Skin skin) {
        byte[] texture = skin.getTexture();
        writeVarintLengthString(buf, skin.getType());
        Varints.encodeUnsigned(buf, texture.length);
        buf.writeBytes(texture);
    }

    public static TranslatedMessage readTranslatedMessage(ByteBuf buf) {
        String message = readVarintLengthString(buf);
        int ln = buf.readByte();
        List<String> replacements = new ArrayList<>();
        for (int i = 0; i < ln; i++) {
            replacements.add(readVarintLengthString(buf));
        }
        return new TranslatedMessage(message, replacements);
    }

    public static void writeTranslatedMessage(ByteBuf buf, TranslatedMessage message) {
        writeVarintLengthString(buf, message.getName());
        buf.writeByte(message.getReplacements().size());
        for (String s : message.getReplacements()) {
            writeVarintLengthString(buf, s);
        }
    }

    public static ItemStack readItemStack(ByteBuf buf) {
        int id = Varints.decodeSigned(buf);
        if (id == 0) {
            return new VoxelwindItemStack(BlockTypes.AIR, 1, null);
        }

        int aux = Varints.decodeSigned(buf);
        int damage = aux >> 8;
        int count = aux & 0xff;
        short nbtSize = buf.readShort();

        ItemType type = ItemTypes.forId(id);

        ItemStackBuilder builder = new VoxelwindItemStackBuilder()
                .itemType(type)
                .itemData(MetadataSerializer.deserializeMetadata(type, (short) damage))
                .amount(count);

        if (nbtSize > 0) {
            try (NBTReader reader = new NBTReader(new ByteBufInputStream(buf.readSlice(nbtSize).order(ByteOrder.LITTLE_ENDIAN)))) {
                Tag<?> tag = reader.readTag();
                if (tag instanceof CompoundTag) {
                    VoxelwindNBTUtils.applyItemData(builder, ((CompoundTag) tag).getValue());
                }
            } catch (IOException e) {
                throw new IllegalStateException("Unable to load NBT data", e);
            }
        }
        return builder.build();
    }

    public static void writeItemStack(ByteBuf buf, ItemStack stack) {
        if (stack == null || stack.getItemType() == BlockTypes.AIR) {
            buf.writeByte(0); // 0 byte means 0 in varint
            return;
        }

        Varints.encodeSigned(buf, stack.getItemType().getId());
        short metadataValue = MetadataSerializer.serializeMetadata(stack);
        Varints.encodeSigned(buf, (metadataValue << 8) | stack.getAmount());

        // Remember this position, since we'll be writing the true NBT size here later:
        int sizeIndex = buf.writerIndex();
        buf.writeShort(0);
        int afterSizeIndex = buf.writerIndex();

        if (stack instanceof VoxelwindItemStack) {
            try (NBTWriter stream = new NBTWriter(new ByteBufOutputStream(buf.order(ByteOrder.LITTLE_ENDIAN)))) {
                stream.write(((VoxelwindItemStack) stack).toSpecificNBT());
            } catch (IOException e) {
                // This shouldn't happen (as this is backed by a Netty ByteBuf), but okay...
                throw new IllegalStateException("Unable to save NBT data", e);
            }

            // Set to the written NBT size
            buf.setShort(sizeIndex, buf.writerIndex() - afterSizeIndex);
        }
    }

    public static UUID readUuid(ByteBuf buf) {
        return new UUID(buf.readLong(), buf.readLong());
    }

    public static void writeUuid(ByteBuf buf, UUID uuid) {
        buf.writeLong(uuid.getMostSignificantBits());
        buf.writeLong(uuid.getLeastSignificantBits());
    }

    public static Rotation readRotation(ByteBuf buffer) {
        float yaw = buffer.readFloat();
        float headYaw = buffer.readFloat();
        float pitch = buffer.readFloat();
        return new Rotation(pitch, yaw, headYaw);
    }

    public static void writeRotation(ByteBuf buffer, Rotation rotation) {
        buffer.writeFloat(rotation.getYaw());
        buffer.writeFloat(rotation.getHeadYaw());
        buffer.writeFloat(rotation.getPitch());
    }

    public static Rotation readByteRotation(ByteBuf buf) {
        byte pitchByte = buf.readByte();
        byte yawByte = buf.readByte();
        byte headYawByte = buf.readByte();
        return new Rotation(rotationByteToAngle(pitchByte), rotationByteToAngle(yawByte), rotationByteToAngle(headYawByte));
    }

    public static void writeByteRotation(ByteBuf buf, Rotation rotation) {
        buf.writeByte(rotationAngleToByte(rotation.getPitch()));
        buf.writeByte(rotationAngleToByte(rotation.getYaw()));
        buf.writeByte(rotationAngleToByte(rotation.getHeadYaw()));
    }

    private static byte rotationAngleToByte(float angle) {
        return (byte) Math.ceil(angle / 360 * 255);
    }

    private static float rotationByteToAngle(byte angle) {
        return angle / 255f * 360f;
    }

    public static void writeResourcePackInfo(ByteBuf buf, ResourcePackInfo info) {
        writeVarintLengthString(buf, info.getPackageId());
        writeVarintLengthString(buf, info.getVersion());
        buf.writeLong(info.getUnknown());
    }

    public static ResourcePackInfo readResourcePackInfo(ByteBuf buf) {
        String pid = readVarintLengthString(buf);
        String v = readVarintLengthString(buf);
        long unknown = buf.readLong();
        return new ResourcePackInfo(pid, v, unknown);
    }
}
