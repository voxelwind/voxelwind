package io.minimum.voxelwind.network.mcpe.annotations;

import io.minimum.voxelwind.network.mcpe.packets.McpeBatch;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This interface marks that this packet should not be wrapped in a {@link McpeBatch}
 * packet under any circumstances. Previously batched packets will be sent before this packet is sent.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface BatchDisallowed {
}
