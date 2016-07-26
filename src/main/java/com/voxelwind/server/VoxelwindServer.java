package com.voxelwind.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.voxelwind.server.network.Native;
import com.voxelwind.server.network.NettyVoxelwindNetworkListener;
import com.voxelwind.server.network.session.SessionManager;
import io.netty.channel.epoll.Epoll;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bouncycastle.jcajce.provider.asymmetric.ec.KeyPairGeneratorSpi;

import java.security.Security;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class VoxelwindServer {
    private static final Logger LOGGER = LogManager.getLogger(VoxelwindServer.class);
    public static final ObjectMapper MAPPER = new ObjectMapper();

    private final SessionManager sessionManager = new SessionManager();
    private final ScheduledExecutorService timerService = Executors.unconfigurableScheduledExecutorService(
            Executors.newSingleThreadScheduledExecutor(new ThreadFactoryBuilder().setNameFormat("Voxelwind Ticker").setDaemon(true).build()));
    private NettyVoxelwindNetworkListener listener;

    public SessionManager getSessionManager() {
        return sessionManager;
    }

    public void boot() throws Exception {
        Security.addProvider( new org.bouncycastle.jce.provider.BouncyCastleProvider() );
        listener = new NettyVoxelwindNetworkListener(this, "0.0.0.0", 19132);
        listener.bind();

        //KeyPairGeneratorSpi

        LOGGER.info("Voxelwind is now running.");

        // NiclasOlofsson recommends using 10ms at maximum
        timerService.scheduleAtFixedRate(sessionManager::onTick, 10, 10, TimeUnit.MILLISECONDS);

        Thread.sleep(10000000);
    }

    public static void main(String... args) throws Exception {
        // RakNet doesn't really like IPv6
        System.setProperty("java.net.preferIPv4Stack", "true");

        if (!Epoll.isAvailable()) {
            LOGGER.error("Your platform does not support epoll. Server throughput and performance may suffer. To resolve this issue, run your server on Linux.");
        }

        if (!Native.zlib.load()) {
            LOGGER.error("Your platform does not support native compression. Server throughput and performance may suffer. To resolve this issue, make sure you're using 64-bit Linux.");
        }

        if (!Native.cipher.load()) {
            LOGGER.error("Your platform does not support native encryption. Server throughput and performance may suffer. To resolve this issue, make sure you're using 64-bit Debian/Ubuntu.");
        }

        VoxelwindServer server = new VoxelwindServer();
        server.boot();
    }
}
