package com.voxelwind.server.network.rcon;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.voxelwind.api.server.Server;
import com.voxelwind.api.server.command.CommandException;
import com.voxelwind.api.server.command.CommandNotFoundException;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class RconHandler extends SimpleChannelInboundHandler<RconMessage> {
    private static final Logger LOGGER = LogManager.getLogger(RconHandler.class);
    private final byte[] password;
    private final Server server;
    private final NettyVoxelwindRconListener listener;
    private boolean authenticated = false;

    public RconHandler(byte[] password, Server server, NettyVoxelwindRconListener listener) {
        this.password = password;
        this.server = server;
        this.listener = listener;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RconMessage message) throws Exception {
        if (!authenticated) {
            Preconditions.checkArgument(message.getType() == RconMessage.SERVERDATA_AUTH, "Trying to handle unauthenticated RCON message!");
            byte[] providedPassword = message.getBody().getBytes(StandardCharsets.UTF_8);
            // Send an empty SERVERDATA_RESPONSE_VALUE to emulate SRCDS
            ctx.writeAndFlush(new RconMessage(message.getId(), RconMessage.SERVERDATA_RESPONSE_VALUE, ""));
            // Check the password.
            if (MessageDigest.isEqual(password, providedPassword)) {
                authenticated = true;
                ctx.writeAndFlush(new RconMessage(message.getId(), RconMessage.SERVERDATA_AUTH_RESPONSE, ""));
            } else {
                ctx.writeAndFlush(new RconMessage(-1, RconMessage.SERVERDATA_AUTH_RESPONSE, ""));
            }
        } else {
            Preconditions.checkArgument(message.getType() == RconMessage.SERVERDATA_EXECCOMMAND, "Trying to handle non-execute command RCON message for authenticated connection!");
            Channel channel = ctx.channel();
            listener.getCommandExecutionService().execute(() -> {
                String body;
                try {
                    RconCommandExecutorSource source = new RconCommandExecutorSource();
                    server.getCommandManager().executeCommand(source, message.getBody());
                    source.stopOutput();

                    body = Joiner.on('\n').join(source.getOutput());
                } catch (CommandNotFoundException e) {
                    body = "No such command found.";
                } catch (CommandException e) {
                    LOGGER.error("Unable to run command {} for remote connection {}", message.getBody(), channel.remoteAddress(),
                            e);
                    body = "An error has occurred. Information has been logged to the console.";
                }
                channel.writeAndFlush(new RconMessage(message.getId(), RconMessage.SERVERDATA_RESPONSE_VALUE, body));
            });
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOGGER.error("An error occurred whilst handling a RCON request from {}", ctx.channel().remoteAddress(), cause);
        // Better to close the channel instead.
        ctx.close();
    }
}
