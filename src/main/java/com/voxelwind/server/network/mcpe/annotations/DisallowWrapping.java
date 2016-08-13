package com.voxelwind.server.network.mcpe.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is used to ignore wrapping this packet in a {@link com.voxelwind.server.network.mcpe.packets.McpeWrapper}. This is used
 * primarily for RakNet packets sent during a player session, such as pong packets.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface DisallowWrapping {
}
