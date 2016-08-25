package com.voxelwind.server.network.rcon;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.voxelwind.api.server.Server;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.ByteOrder;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class NettyVoxelwindRconListener extends ChannelInitializer<SocketChannel> {
    private static final Logger LOGGER = LogManager.getLogger(NettyVoxelwindRconListener.class);

    private final Server server;
    private final EventLoopGroup group;
    private final ExecutorService commandExecutionService = Executors.newSingleThreadExecutor(
            new ThreadFactoryBuilder().setDaemon(true).setNameFormat("Voxelwind RCON Command Executor").build());
    private final byte[] password;

    public NettyVoxelwindRconListener(Server server, byte[] password) {
        this.server = server;
        this.password = password;
        ThreadFactory factory = new ThreadFactoryBuilder().setDaemon(true).setNameFormat("Voxelwind RCON Listener").build();
        this.group = Epoll.isAvailable() ? new EpollEventLoopGroup(1, factory) : new NioEventLoopGroup(1, factory);
    }

    @Override
    protected void initChannel(SocketChannel channel) throws Exception {
        // Maximum 4KB input size. You're administrating a server, not running a proxy!
        channel.pipeline().addLast("lengthDecoder", new LengthFieldBasedFrameDecoder(ByteOrder.LITTLE_ENDIAN, 4096, 0, 4, 0, 4, true));
        channel.pipeline().addLast("rconDecoder", new RconDecoder());
        channel.pipeline().addLast("rconHandler", new RconHandler(password, server, this));
        channel.pipeline().addLast("rconEncoder", new RconEncoder());
    }

    public ExecutorService getCommandExecutionService() {
        return commandExecutionService;
    }

    public void bind(String host, int port) {
        new ServerBootstrap()
                .channel(Epoll.isAvailable() ? EpollServerSocketChannel.class : NioServerSocketChannel.class)
                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .group(group)
                .childHandler(this)
                .bind(host, port)
                .addListener((ChannelFutureListener) future -> {
                    if (future.isSuccess()) {
                        LOGGER.info("RCON listening on port {}", future.channel().localAddress());
                    } else {
                        LOGGER.info("RCON can't bind", future.cause());
                    }
                });
    }
}
