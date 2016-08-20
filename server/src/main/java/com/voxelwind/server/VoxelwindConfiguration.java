package com.voxelwind.server;

public class VoxelwindConfiguration {
    /**
     * Whether or not to use Xbox authentication. This setting will be overridden on a system that does not have the
     * unlimited strength JCE policy installed and 64-bit Linux is not in use. Enabling this setting will also encrypt
     * all player connections.
     */
    private boolean performXboxAuthentication;
    /**
     * The host name or IP address Voxelwind will bind to. By default, Voxelwind will bind to 0.0.0.0;
     */
    private String bindHost;
    /**
     * The port number Voxelwind will bind to. By default, Voxelwind will bind to port 19132.
     */
    private int port;
}
