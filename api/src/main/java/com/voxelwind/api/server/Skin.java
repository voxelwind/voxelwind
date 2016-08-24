package com.voxelwind.api.server;

import com.google.common.base.Preconditions;
import lombok.NonNull;
import lombok.Value;

import javax.annotation.Nonnull;
import java.awt.image.BufferedImage;

/**
 * Represents a player's skin.
 */
@Value
@Nonnull
public class Skin {
    @NonNull
    private final String type;
    @NonNull
    private final byte[] texture;

    public static Skin create(BufferedImage image) {
        Preconditions.checkNotNull(image, "image");
        Preconditions.checkArgument(image.getHeight() == 32 && image.getWidth() == 64, "Image is not 32x64");

        byte[] mcpeTexture = new byte[32 * 64 * 4];

        int at = 0;
        for (int i = 0; i < image.getHeight(); i++) {
            for (int i1 = 0; i1 < image.getWidth(); i1++) {
                int rgb = image.getRGB(i, i1);
                mcpeTexture[at++] = (byte) ((rgb & 0x00ff0000) >> 16);
                mcpeTexture[at++] = (byte) ((rgb & 0x0000ff00) >> 8);
                mcpeTexture[at++] = (byte) (rgb & 0x000000ff);
                mcpeTexture[at++] = (byte) ((rgb >> 24) & 0xff);
            }
        }

        return new Skin("Standard_Custom", mcpeTexture);
    }
}
