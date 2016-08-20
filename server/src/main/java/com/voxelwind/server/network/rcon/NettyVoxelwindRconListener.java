package com.voxelwind.server.network.rcon;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.voxelwind.api.server.Server;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NettyVoxelwindRconListener extends ChannelInitializer<SocketChannel> {
    private static final Logger LOGGER = LogManager.getLogger(NettyVoxelwindRconListener.class);

    private final Server server;
    private final NioEventLoopGroup group = new NioEventLoopGroup(1, new ThreadFactoryBuilder().setDaemon(true)
            .setNameFormat("Voxelwind RCON Listener").build());
    private final ExecutorService commandExecutionService = Executors.newSingleThreadExecutor(
            new ThreadFactoryBuilder().setDaemon(true).setNameFormat("Voxelwind RCON Command Executor").build());

    public NettyVoxelwindRconListener(Server server) {
        this.server = server;
    }

    @Override
    protected void initChannel(SocketChannel channel) throws Exception {
        // Maximum 4KB input size. You're administrating a server, not running a proxy!
        channel.pipeline().addLast("lengthDecoder", new LengthFieldBasedFrameDecoder(ByteOrder.LITTLE_ENDIAN, 4096, 0, 2, 0, 2, true));
        channel.pipeline().addLast("rconCodec", new RconCodec());
        channel.pipeline().addLast("rconHandler", new RconHandler("test".getBytes(StandardCharsets.UTF_8), server, this));
        channel.pipeline().addLast("lengthPrepender", new LengthFieldPrepender(ByteOrder.LITTLE_ENDIAN, 4, 0, false));
    }

    public ExecutorService getCommandExecutionService() {
        return commandExecutionService;
    }

    public void bind(String host, int port) {
        new ServerBootstrap()
                .channel(NioServerSocketChannel.class)
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
