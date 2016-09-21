package com.voxelwind.server.network.listeners;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.voxelwind.server.VoxelwindServer;
import com.voxelwind.server.network.raknet.DatagramRakNetPacketCodec;
import com.voxelwind.server.network.raknet.SimpleRakNetPacketCodec;
import com.voxelwind.server.network.raknet.handler.RakNetDatagramHandler;
import com.voxelwind.server.network.raknet.handler.RakNetDirectPacketHandler;
import com.voxelwind.server.network.raknet.handler.TailHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollChannelOption;
import io.netty.channel.epoll.EpollDatagramChannel;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.nio.NioDatagramChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.InetSocketAddress;

public class McpeOverRakNetNetworkListener extends ChannelInitializer<DatagramChannel> implements NetworkListener {
    private static final Logger LOGGER = LogManager.getLogger(McpeOverRakNetNetworkListener.class);
    private final Bootstrap bootstrap;
    private final InetSocketAddress address;
    private final VoxelwindServer server;
    private final boolean useSoReuseport;
    private DatagramChannel channel;

    public McpeOverRakNetNetworkListener(VoxelwindServer voxelwindServer, String host, int port, boolean useSoReuseport) {
        this.server = voxelwindServer;
        this.address = new InetSocketAddress(host, port);
        this.useSoReuseport = useSoReuseport;
        if (Epoll.isAvailable()) {
            bootstrap = new Bootstrap()
                    .channel(EpollDatagramChannel.class)
                    .group(new EpollEventLoopGroup(0, new ThreadFactoryBuilder().setDaemon(true).setNameFormat("Voxelwind MCPE Listener - #%d").build()))
                    .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                    .handler(this);
            if (useSoReuseport) {
                bootstrap.option(EpollChannelOption.SO_REUSEPORT, true);
            }
        } else {
            bootstrap = new Bootstrap()
                    .channel(NioDatagramChannel.class)
                    .group(new NioEventLoopGroup(0, new ThreadFactoryBuilder().setDaemon(true).setNameFormat("Voxelwind MCPE Listener - #%d").build()))
                    .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                    .handler(this);
        }
    }

    @Override
    protected void initChannel(DatagramChannel channel) throws Exception {
        this.channel = channel;
        channel.pipeline()
                .addLast("simpleRaknetHandler", new SimpleRakNetPacketCodec())
                .addLast("raknetDirectPacketHandler", new RakNetDirectPacketHandler(server))
                .addLast("raknetDatagramHandler", new DatagramRakNetPacketCodec(server))
                .addLast("voxelwindDatagramHandler", new RakNetDatagramHandler(server))
                .addLast("tailHandler", new TailHandler());
    }

    @Override
    public boolean bind() {
        boolean success = false;
        if (Epoll.isAvailable() && useSoReuseport) {
            // Can use SO_REUSEPORT so that multiple threads can bind to a port.
            for (int i = 0; i < Runtime.getRuntime().availableProcessors(); i++) {
                try {
                    ChannelFuture future = bootstrap.bind(address).await();
                    if (future.isSuccess()) {
                        LOGGER.debug("Bound listener #" + i + " for " + address);
                        success = true;
                    } else {
                        LOGGER.error("Unable to bind listener #" + i + " for " + address, future.cause());
                        // Continue - as long as we have at least one listener open, we're okay.
                    }
                } catch (InterruptedException e) {
                    LOGGER.info("Interrupted while waiting for bind");
                }
            }
            return success;
        } else {
            try {
                ChannelFuture future = bootstrap.bind(address).await();
                return future.isSuccess();
            } catch (InterruptedException e) {
                LOGGER.info("Interrupted while waiting for bind");
            }
        }
        return false;
    }

    @Override
    public void close() {
        bootstrap.group().shutdownGracefully();
        if (channel != null) {
            channel.close().syncUninterruptibly();
        }
    }

    public InetSocketAddress getAddress() {
        return address;
    }
}
