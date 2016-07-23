package io.minimum.voxelwind.network.raknet;

import javax.xml.bind.DatatypeConverter;

public class RakNetConstants {
    public static final byte[] RAKNET_UNCONNECTED_MAGIC = DatatypeConverter.parseHexBinary("00ffff00fefefefefdfdfdfd12345678");

    private RakNetConstants() {

    }
}
