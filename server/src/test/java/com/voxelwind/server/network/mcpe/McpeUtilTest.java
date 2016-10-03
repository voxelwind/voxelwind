package com.voxelwind.server.network.mcpe;

import com.flowpowered.math.vector.Vector3f;
import com.flowpowered.math.vector.Vector3i;
import com.voxelwind.api.server.player.TranslatedMessage;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.*;

public class McpeUtilTest {
    @Test
    public void leLengthString() throws Exception {
        ByteBuf dest = Unpooled.buffer();
        McpeUtil.writeLELengthString(dest, "test");
        assertEquals("test", McpeUtil.readLELengthString(dest));
        dest.release();
    }

    @Test
    public void readVector3i() throws Exception {
        ByteBuf dest = Unpooled.buffer();
        Vector3i test = new Vector3i(1, 2, 3);
        McpeUtil.writeVector3i(dest, test, false); // y is an int
        assertEquals(test, McpeUtil.readVector3i(dest, false));

        ByteBuf dest2 = Unpooled.buffer();
        McpeUtil.writeVector3i(dest2, test, true); // y is a byte
        assertEquals(test, McpeUtil.readVector3i(dest2, true));

        dest.release();
        dest2.release();
    }

    @Test
    public void readVector3f() throws Exception {
        ByteBuf dest = Unpooled.buffer();
        Vector3f test = new Vector3f(1, 2, 3);
        McpeUtil.writeVector3f(dest, test);
        assertEquals(test, McpeUtil.readVector3f(dest));
        dest.release();
    }

    /*@Test
    public void readAttributes() throws Exception {

    }

    @Test
    public void readSkin() throws Exception {

    }*/

    @Test
    public void readTranslatedMessage() throws Exception {
        TranslatedMessage message = new TranslatedMessage("test", "a", "b");
        ByteBuf dest = Unpooled.buffer();
        McpeUtil.writeTranslatedMessage(dest, message);
        assertEquals(message, McpeUtil.readTranslatedMessage(dest));
        dest.release();
    }

    /*@Test
    public void readItemStack() throws Exception {

    }*/

    @Test
    public void readUuid() throws Exception {
        UUID uuid = UUID.randomUUID();
        ByteBuf dest = Unpooled.buffer();
        McpeUtil.writeUuid(dest, uuid);
        assertEquals(uuid, McpeUtil.readUuid(dest));
        dest.release();
    }
}