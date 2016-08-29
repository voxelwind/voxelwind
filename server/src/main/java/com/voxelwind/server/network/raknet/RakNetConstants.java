package com.voxelwind.server.network.raknet;

import lombok.experimental.UtilityClass;

import javax.xml.bind.DatatypeConverter;

@UtilityClass
public class RakNetConstants {
    public static final byte[] RAKNET_UNCONNECTED_MAGIC = DatatypeConverter.parseHexBinary("00ffff00fefefefefdfdfdfd12345678");
}
