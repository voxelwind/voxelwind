package com.voxelwind.server.network.listeners;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.voxelwind.server.VoxelwindServer;
import com.voxelwind.server.network.rcon.RconDecoder;
import com.voxelwind.server.network.rcon.RconEncoder;
import com.voxelwind.server.network.rcon.RconHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;

import java.nio.ByteOrder;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

public class RconNetworkListener extends ChannelInitializer<SocketChannel> implements NetworkListener {
    private final VoxelwindServer server;
    private final EventLoopGroup group;
    private final ExecutorService commandExecutionService = Executors.newSingleThreadExecutor(
            new ThreadFactoryBuilder().setDaemon(true).setNameFormat("Voxelwind RCON Command Executor").build());
    private final byte[] password;
    private Channel channel;

    public RconNetworkListener(VoxelwindServer server, byte[] password) {
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
        channel.pipeline().addLast("lengthPrepender", new LengthFieldPrepender(ByteOrder.LITTLE_ENDIAN, 4, 0, false));
        channel.pipeline().addLast("rconEncoder", new RconEncoder());
    }

    public ExecutorService getCommandExecutionService() {
        return commandExecutionService;
    }

    @Override
    public boolean bind() {
        ChannelFuture future = new ServerBootstrap()
                .channel(Epoll.isAvailable() ? EpollServerSocketChannel.class : NioServerSocketChannel.class)
                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .group(group)
                .childHandler(this)
                .bind(server.getConfiguration().getRcon().getHost(), server.getConfiguration().getRcon().getPort())
                .awaitUninterruptibly();

        if (future.isSuccess()) {
            this.channel = future.channel();
            return true;
        }

        return false;
    }

    @Override
    public void close() {
        commandExecutionService.shutdown();
        try {
            commandExecutionService.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            // not handling
        }
        commandExecutionService.shutdownNow();
        group.shutdownGracefully();
        if (channel != null) {
            channel.close();
        }
    }
}
