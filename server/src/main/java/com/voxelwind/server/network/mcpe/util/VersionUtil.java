package com.voxelwind.server.network.mcpe.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Arrays;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class VersionUtil {
    private static final int[] COMPATIBLE_PROTOCOL_VERSIONS = new int[]{ 113 };

    public static int[] getCompatibleProtocolVersions() {
        return COMPATIBLE_PROTOCOL_VERSIONS.clone();
    }

    public static boolean isCompatible(int protocolVersion) {
        return Arrays.binarySearch(COMPATIBLE_PROTOCOL_VERSIONS, protocolVersion) >= 0;
    }

    public static String getHumanVersionName(int protocolVersion) {
        switch (protocolVersion) {
            case 113:
                return "1.1";
        }
        return null;
    }
}
