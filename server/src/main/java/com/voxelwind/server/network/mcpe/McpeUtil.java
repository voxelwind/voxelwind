package com.voxelwind.server.network.mcpe;

import com.flowpowered.math.vector.Vector3f;
import com.flowpowered.math.vector.Vector3i;
import com.flowpowered.nbt.stream.NBTInputStream;
import com.flowpowered.nbt.stream.NBTOutputStream;
import com.google.common.base.Preconditions;
import com.voxelwind.api.game.item.ItemStack;
import com.voxelwind.api.game.item.ItemType;
import com.voxelwind.api.game.item.ItemTypes;
import com.voxelwind.api.game.item.data.ItemData;
import com.voxelwind.api.game.level.block.types.BlockTypes;
import com.voxelwind.api.server.Skin;
import com.voxelwind.api.server.util.TranslatedMessage;
import com.voxelwind.server.game.item.VoxelwindItemStack;
import com.voxelwind.server.game.level.util.Attribute;
import com.voxelwind.server.network.raknet.RakNetUtil;
import com.voxelwind.api.util.Rotation;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.ByteBufUtil;

import java.io.IOException;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class McpeUtil {
    private McpeUtil() {

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

    public static void writeVector3i(ByteBuf buf, Vector3i vector3i) {
        writeVector3i(buf, vector3i, true);
    }

    public static void writeVector3i(ByteBuf buf, Vector3i vector3i, boolean yIsByte) {
        buf.writeInt(vector3i.getX());
        if (yIsByte) {
            buf.writeInt(vector3i.getZ());
            buf.writeByte(vector3i.getY());
        } else {
            buf.writeInt(vector3i.getY());
            buf.writeInt(vector3i.getZ());
        }
    }

    public static Vector3i readVector3i(ByteBuf buf) {
        return readVector3i(buf, false);
    }

    public static Vector3i readVector3i(ByteBuf buf, boolean yIsByte) {
        int x = buf.readInt();
        int y;
        int z;
        if (yIsByte) {
            z = buf.readInt();
            y = buf.readByte();
        } else {
            y = buf.readInt();
            z = buf.readInt();
        }
        return new Vector3i(x, y, z);
    }

    public static void writeVector3f(ByteBuf buf, Vector3f vector3f) {
        buf.writeFloat(vector3f.getX());
        buf.writeFloat(vector3f.getY());
        buf.writeFloat(vector3f.getZ());
    }

    public static Vector3f readVector3f(ByteBuf buf) {
        double x = buf.readFloat();
        double y = buf.readFloat();
        double z = buf.readFloat();
        return new Vector3f(x, y, z);
    }

    public static Rotation readRotation(ByteBuf buf) {
        byte pitchByte = buf.readByte();
        byte yawByte = buf.readByte();
        byte headYawByte = buf.readByte();
        return new Rotation(rotationByteToAngle(pitchByte), rotationByteToAngle(yawByte), rotationByteToAngle(headYawByte));
    }

    public static void writeRotation(ByteBuf buf, Rotation rotation) {
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

    public static Collection<Attribute> readAttributes(ByteBuf buf) {
        List<Attribute> attributes = new ArrayList<>();
        short size = buf.readShort();

        for (int i = 0; i < size; i++) {
            float min = buf.readFloat();
            float max = buf.readFloat();
            float val = buf.readFloat();
            String name = RakNetUtil.readString(buf);

            attributes.add(new Attribute(name, min, max, val));
        }

        return attributes;
    }

    public static void writeAttributes(ByteBuf buf, Collection<Attribute> attributeList) {
        buf.writeShort(attributeList.size());
        for (Attribute attribute : attributeList) {
            buf.writeFloat(attribute.getMinimumValue());
            buf.writeFloat(attribute.getMaximumValue());
            buf.writeFloat(attribute.getValue());
            RakNetUtil.writeString(buf, attribute.getName());
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
        RakNetUtil.writeString(buf, skin.getType());
        buf.writeShort(texture.length);
        buf.writeBytes(texture);
    }

    public static TranslatedMessage readTranslatedMessage(ByteBuf buf) {
        String message = RakNetUtil.readString(buf);
        int ln = buf.readByte();
        List<String> replacements = new ArrayList<>();
        for (int i = 0; i < ln; i++) {
            replacements.add(RakNetUtil.readString(buf));
        }
        return new TranslatedMessage(message, replacements);
    }

    public static void writeTranslatedMessage(ByteBuf buf, TranslatedMessage message) {
        RakNetUtil.writeString(buf, message.getName());
        buf.writeByte(message.getReplacements().size());
        for (String s : message.getReplacements()) {
            RakNetUtil.writeString(buf, s);
        }
    }

    public static ItemStack readItemStack(ByteBuf buf) {
        short id = buf.readShort();
        if (id == 0) {
            return new VoxelwindItemStack(BlockTypes.AIR, 1, null);
        }

        int count = buf.readByte();
        short damage = buf.readShort();

        short nbtSize = buf.readShort();

        ItemType type = ItemTypes.forId(id);
        VoxelwindItemStack stack = new VoxelwindItemStack(type, count, type.createDataFor(damage).orElse(null));

        if (nbtSize > 0) {
            try (NBTInputStream stream = new NBTInputStream(new ByteBufInputStream(buf.readSlice(nbtSize)), false, ByteOrder.LITTLE_ENDIAN)) {
                stack.readNbt(stream);
            } catch (IOException e) {
                throw new IllegalStateException("Unable to load NBT data", e);
            }
        }
        return stack;
    }

    public static void writeItemStack(ByteBuf buf, ItemStack stack) {
        buf.writeShort(stack.getItemType().getId());
        if (stack.getItemType() == BlockTypes.AIR) {
            return;
        }

        buf.writeByte(stack.getAmount());
        Optional<ItemData> dataOptional = stack.getItemData();
        if (dataOptional.isPresent()) {
            buf.writeShort(dataOptional.get().toMetadata());
        } else {
            buf.writeShort(0);
        }

        // Remember this position, since we'll be writing the true NBT size here later:
        int sizeIndex = buf.writerIndex();
        buf.writeShort(0);

        if (stack instanceof VoxelwindItemStack) {
            try (NBTOutputStream stream = new NBTOutputStream(new ByteBufOutputStream(buf), false, ByteOrder.LITTLE_ENDIAN)) {
                ((VoxelwindItemStack) stack).writeNbt(stream);
            } catch (IOException e) {
                // This shouldn't happen (as this is backed by a Netty ByteBuf), but okay...
                throw new IllegalStateException("Unable to save NBT data", e);
            }

            // Set to the written NBT size
            buf.setShort(sizeIndex, buf.writerIndex() - sizeIndex);
        }
    }

    public static UUID readUuid(ByteBuf buf) {
        return new UUID(buf.readLong(), buf.readLong());
    }

    public static void writeUuid(ByteBuf buf, UUID uuid) {
        buf.writeLong(uuid.getMostSignificantBits());
        buf.writeLong(uuid.getLeastSignificantBits());
    }
}
